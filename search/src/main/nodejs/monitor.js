'use strict';

const utils = require('./utils');
const AWS = require('aws-sdk');
const moment = require('moment');

const s3 = new AWS.S3();
const DEBUG = false;
const SEARCH_TIMEOUT_SECS = process.env.SEARCH_TIMEOUT_SECS;

exports.isSearchDone = async (event, context) => {

    // Parameters
    if (DEBUG) console.log(event);
    const bucket = event.bucket;
    const prefix = event.prefix;

    if (bucket == null) {
        throw new Error('Missing required key \'bucket\' in input to isSearchDone');
    }

    if (prefix == null) {
        throw new Error('Missing required key \'prefix\' in input to isSearchDone');
    }

    // Fetch metadata
    const metadata = await utils.getObject(s3, bucket, prefix+"/metadata.json");
    const numPartitions = metadata['partitions'];
    const startTime = moment(metadata['startTime']);

    // Fetch all keys
    const allKeys = new Set(await utils.getAllKeys(s3, { Bucket: bucket, Prefix: prefix }));
    if (DEBUG) console.log(allKeys);

    // Check if all partitions have completed
    let numComplete = 0;
    for(let i=0; i<numPartitions; i++) {
        const batchId = i.toString().padStart(4,"0");
        const outputKey = prefix+"/batch_"+batchId+".json";
        if (allKeys.has(outputKey)) {
            numComplete++;
        }
    }

    const completed = numPartitions === numComplete;
    const numRemaining = numPartitions - numComplete;

    // Calculate total search time
    const now = new Date();
    const endTime = moment(now.toISOString());
    const elapsedSecs = endTime.diff(startTime, "s");
    
    if (completed) {
        console.log(`Search complete: ${numComplete}/${numPartitions}`);

        // Reduce results
        const allMatches = [];
        for(let i=0; i<numPartitions; i++) {
            const batchId = i.toString().padStart(4,"0");
            const outputKey = prefix+"/batch_"+batchId+".json";
            const batchResults = await utils.getObject(s3, bucket, outputKey);
            for(var j=0; j<batchResults.length; j++) {
                var maskMatches;
                if (i==0) {
                    maskMatches = [];
                    allMatches.push(maskMatches);
                }
                else {
                    maskMatches = allMatches[j];
                }
                for(const result of batchResults[j]) {
                    maskMatches.push(result);
                }
            }
        }

        // Sort the matches by descending score
        let totalMatches = 0;
        for(let i=0; i<allMatches.length; i++) {
            allMatches[i] = allMatches[i].sort((a, b) => b.score - a.score);
            console.log(`Found ${allMatches[i].length} total matches for Mask #${i}`);
            totalMatches += allMatches[i].length;
        }

        // Write matches to a file
        const outputUri = await utils.putObject(s3, bucket, prefix+"/results.json", allMatches);
        console.log(`Saved reduced matches to ${outputUri}`);

        // TODO: delete individual batch result files, which are no longer needed

        console.log(`Search took ${elapsedSecs} seconds`);
        return {
            ...event,
            elapsedSecs: elapsedSecs,
            numPartitions: numPartitions,
            numRemaining: 0,
            totalMatches: totalMatches,
            completed: true,
            timedOut: false
        };
    }
    else if (elapsedSecs > SEARCH_TIMEOUT_SECS) {
        console.log(`Search timed out after ${elapsedSecs} seconds`);
        return {
            ...event,
            elapsedSecs: elapsedSecs,
            numPartitions: numPartitions,
            numRemaining: numRemaining,
            completed: false,
            timedOut: true
        };
    }
    else {
        console.log(`Search still running after ${elapsedSecs} seconds. Completed ${numComplete} of ${numPartitions} jobs.`);
        return {
            ...event,
            elapsedSecs: elapsedSecs,
            numPartitions: numPartitions,
            numRemaining: numRemaining,
            completed: false,
            timedOut: false
        };
    }

}