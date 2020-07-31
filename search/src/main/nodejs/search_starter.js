'use strict';

const AWS = require("aws-sdk");

const SEARCH_TABLE = process.env.SEARCH_TABLE;

AWS.config.update({
    apiVersion: '2012-08-10'
});

const dynamoDocClient = new AWS.DynamoDB.DocumentClient();

const startColorDepthSearch = async (searchParams) => {
    console.log('Start ColorDepthSearch', searchParams);
}

const getSearch = async (id) => {
    console.log('Get Search', id);
    const params = {
        TableName: `${SEARCH_TABLE}`,
        Key: {
            'id': id
        }
    };
    console.log('GetItem', params);
    const searchData = await dynamoDocClient.get(params).promise();
    console.log('Found search', searchData);
    return searchData ? searchData.Item : null;
}

const getNewRecords = async (e) => {
    const newRecordsPromises = await e.Records
        .filter(r => r.eventName === 'INSERT')
        .map(r => r.dynamodb)
        .map(r => r.Keys.id.S)
        .map(async searchId => await getSearch(searchId));
    return await Promise.all(newRecordsPromises);
}

exports.searchStarter = async (event) => {
    console.log(event);
    const newRecords = await getNewRecords(event);
    newRecords.forEach(r => {
        startColorDepthSearch(r);
    });
};

