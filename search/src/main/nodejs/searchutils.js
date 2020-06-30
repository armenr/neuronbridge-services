'use strict'

const getKeyWithNoExt = (searchInputKey) => {
    const extSeparatorIndex = searchInputKey.lastIndexOf('.');
    return extSeparatorIndex > 0 ? searchInputKey.substring(0, extSeparatorIndex) : searchInputKey;
}

exports.getSearchParamsKey = (searchInputName) =>  {
    const searchInputKey = getKeyWithNoExt(searchInputName);
    return `${searchInputKey}.search`;
}

exports.getSearchMetadataKey = (searchInputName) => {
    const searchInputKey = getKeyWithNoExt(searchInputName);
    return `${searchInputKey}.metadata`;
}

exports.getSearchProgressKey = (searchInputName) => {
    const searchInputKey = getKeyWithNoExt(searchInputName);
    return `${searchInputKey}.progress`;
}

exports.getSearchResultsKey = (searchInputName) => {
    const searchInputKey = getKeyWithNoExt(searchInputName);
    return `${searchInputKey}.result`;
}

const getIntermediateSearchResultsPrefix = (searchInputName) => {
    const searchInputKey = getKeyWithNoExt(searchInputName);
    const searchInputPathComps = searchInputKey.split('/');
    if (!searchInputPathComps.length) {
        return `results`;
    } else {
        return searchInputPathComps.slice(0, -1).join('/')+`/results`;
    }
}

const getIntermediateSearchResultsKey = (searchInputName, batchNumber) => {
    const intermediateSearchResultsPrefix = getIntermediateSearchResultsPrefix(searchInputName);
    const batchId = 'batch_' + batchNumber.toString().padStart(4,"0") + '.json';
    return `${intermediateSearchResultsPrefix}/${batchId}`;
}

exports.getIntermediateSearchResultsPrefix = getIntermediateSearchResultsPrefix;
exports.getIntermediateSearchResultsKey = getIntermediateSearchResultsKey;
