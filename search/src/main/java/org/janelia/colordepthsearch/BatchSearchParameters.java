package org.janelia.colordepthsearch;

import java.util.List;

/**
 * Search parameters for a color depth search with multiple masks.
 *
 * @author <a href="mailto:rokickik@janelia.hhmi.org">Konrad Rokicki</a>
 */
public class BatchSearchParameters {

    private String monitorName;
    private String jobId;
    private Integer batchId;
    private String searchPrefix;
    private List<String> searchKeys;
    private String maskPrefix;
    private List<String> maskKeys;
    private Integer dataThreshold = 100;
    private List<Integer> maskThresholds;
    private Double pixColorFluctuation = 2.0;
    private Integer xyShift = 0;
    private boolean mirrorMask = false;
    private String outputURI;
    private Double minMatchingPixRatio = 2.;

    public String getMonitorName() {
        return monitorName;
    }

    public void setMonitorName(String monitorName) {
        this.monitorName = monitorName;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public String getSearchPrefix() {
        return searchPrefix;
    }

    /**
     * URI or path prefix for retrieving the search images.
     * @param searchPrefix
     */
    public void setSearchPrefix(String searchPrefix) {
        this.searchPrefix = searchPrefix;
    }

    public List<String> getSearchKeys() {
        return searchKeys;
    }

    /**
     * Keys of images to search, relative to the searchPrefix.
     * @param searchKeys
     */
    public void setSearchKeys(List<String> searchKeys) {
        this.searchKeys = searchKeys;
    }

    public String getMaskPrefix() {
        return maskPrefix;
    }

    /**
     * URI or path prefix for retrieving the masks.
     * @param maskPrefix
     */
    public void setMaskPrefix(String maskPrefix) {
        this.maskPrefix = maskPrefix;
    }

    public List<String> getMaskKeys() {
        return maskKeys;
    }

    /**
     * Keys of masks to search with, relative to the maskPrefix.
     * @param maskKeys
     */
    public void setMaskKeys(List<String> maskKeys) {
        this.maskKeys = maskKeys;
    }

    public Integer getDataThreshold() {
        return dataThreshold;
    }

    /**
     * Intensity threshold for ignoring pixels in the data.
     * @param dataThreshold
     */
    public void setDataThreshold(Integer dataThreshold) {
        this.dataThreshold = dataThreshold;
    }

    public List<Integer> getMaskThresholds() {
        return maskThresholds;
    }

    /**
     * Intensity threshold for ignoring pixels in the mask.
     * @param maskThresholds
     */
    public void setMaskThresholds(List<Integer> maskThresholds) {
        this.maskThresholds = maskThresholds;
    }

    public Double getPixColorFluctuation() {
        return pixColorFluctuation;
    }

    /**
     * Set how much color depth (i.e. Z distance) to search.
     * @param pixColorFluctuation
     */
    public void setPixColorFluctuation(Double pixColorFluctuation) {
        this.pixColorFluctuation = pixColorFluctuation;
    }

    public Integer getXyShift() {
        return xyShift;
    }

    /**
     * Set how much to shift the mask in the XY plane when comparing against the search library.
     * @param xyShift
     */
    public void setXyShift(Integer xyShift) {
        this.xyShift = xyShift;
    }

    public boolean isMirrorMask() {
        return mirrorMask;
    }

    /**
     * Set whether or not to mirror the mask across the Y axis.
     * @param mirrorMask
     */
    public void setMirrorMask(boolean mirrorMask) {
        this.mirrorMask = mirrorMask;
    }

    public String getOutputURI() {
        return outputURI;
    }

    /**
     * Set the path to the output file on S3.
     * @param outputURI
     */
    public void setOutputURI(String outputURI) {
        this.outputURI = outputURI;
    }

    public Double getMinMatchingPixRatio() {
        return minMatchingPixRatio;
    }

    public void setMinMatchingPixRatio(Double minMatchingPixRatio) {
        this.minMatchingPixRatio = minMatchingPixRatio;
    }
}
