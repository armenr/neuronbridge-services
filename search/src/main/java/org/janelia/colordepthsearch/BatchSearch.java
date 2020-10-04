package org.janelia.colordepthsearch;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.commons.lang3.StringUtils;
import org.janelia.colormipsearch.api.cdsearch.ColorMIPSearch;
import org.janelia.colormipsearch.api.cdsearch.ColorMIPSearchResult;
import org.janelia.colormipsearch.api.cdsearch.ColorMIPSearchResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.util.List;

/**
 * AWS Lambda Handler that performs a pairwise color depth search between all provided MIPs to be searched and all provided masks.
 * The handler writes down the result to s specified location and returns the number of found matches.
 *
 * @author <a href="mailto:rokickik@janelia.hhmi.org">Konrad Rokicki</a>
 */
public class BatchSearch implements RequestHandler<BatchSearchParameters, Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(BatchSearch.class);

    @Override
    public Integer handleRequest(BatchSearchParameters params, Context context) {
        if (StringUtils.isNotBlank(params.getSearchId())) {
            MDC.put("searchId", params.getSearchId());
        }
        verifyCDSParams(params);
        S3Client s3 = LambdaUtils.createS3();
        List<ColorMIPSearchResult> cdsResults = performColorDepthSearch(params, s3);
        writeCDSResults(cdsResults, s3, params.getOutputURI());
        return cdsResults.size();
    }

    private void verifyCDSParams(BatchSearchParameters params) {
        LOG.debug("Received color depth search request: {}", LambdaUtils.toJson(params));
        LOG.info("Monitor: {}", params.getMonitorName());
        // This next log statement is parsed by the analyzer. DO NOT CHANGE.
        LOG.info("Batch Id: {}", params.getBatchId());
        LOG.info("Searching {} images using {} masks", params.getSearchKeys().size(), params.getMaskKeys().size());
        if (LambdaUtils.isEmpty(params.getSearchKeys())) {
            throw new IllegalArgumentException("No images to search");
        }
        if (LambdaUtils.isEmpty(params.getMaskKeys())) {
            throw new IllegalArgumentException("No masks to search");
        }
        if (LambdaUtils.isEmpty(params.getMaskThresholds())) {
            throw new IllegalArgumentException("No mask thresholds specified");
        }
        if (params.getMaskThresholds().size() != params.getMaskKeys().size()) {
            throw new IllegalArgumentException("Number of mask thresholds does not match number of masks");
        }
    }

    private List<ColorMIPSearchResult> performColorDepthSearch(BatchSearchParameters params, S3Client s3) {

        ColorMIPSearch colorMIPSearch = new ColorMIPSearch(
                0,
                params.getDataThreshold(),
                params.getPixColorFluctuation(),
                params.getXyShift(),
                params.isMirrorMask(),
                params.getMinMatchingPixRatio());
        AWSLambdaColorMIPSearch awsColorMIPSearch = new AWSLambdaColorMIPSearch(
                new AWSMIPLoader(s3),
                colorMIPSearch,
                params.getMaskPrefix(),
                params.getSearchPrefix(),
                LambdaUtils.getOptionalEnv("SEARCHED_THUMBNAILS_BUCKET", params.getSearchPrefix())
        );

        LOG.debug("Comparing {} masks with {} library mips", params.getMaskKeys().size(), params.getSearchKeys().size());
        List<ColorMIPSearchResult> cdsResults = awsColorMIPSearch.findAllColorDepthMatches(
                params.getMaskKeys(),
                params.getMaskThresholds(),
                params.getSearchKeys()
        );
        LOG.info("Found {} matches.", cdsResults.size());

        return cdsResults;
    }

    private void writeCDSResults(List<ColorMIPSearchResult> cdsResults, S3Client s3, String outputLocation) {
        if (outputLocation != null) {
            try {
                LambdaUtils.putObject(
                        s3,
                        URI.create(outputLocation),
                        ColorMIPSearchResultUtils.groupResults(cdsResults, ColorMIPSearchResult::perMaskMetadata));
                LOG.info("Results written to {}", outputLocation);
            } catch (Exception e) {
                throw new IllegalStateException("Error writing results", e);
            }
        }
    }
}
