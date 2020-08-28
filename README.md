# neuronbridge-services
AWS Services for NeuronBridge

## Deployment

As a prerequisite, you need to have the [AWS CLI](https://aws.amazon.com/cli/) installed and configured with proper AWS credentials.

### Deploy NeuronBridge compute alignment stack

NeuronBridge compute alignment requires an AMI instance preconfigured with ECS and with all required volumes mounted as expected by the alignment batch job.

To create the AMI use these steps:
* start an [Amazon ECS-optimized Amazon Linux AMI](https://aws.amazon.com/marketplace/search/results?x=0&y=0&searchTerms=Amazon+ECS-Optimized+Amazon+Linux+AMI&page=1&ref_=nav_search_box). 

* tart the EC2 instance 
* run the following commands that mount the expected volumes: 

```
sudo yum -y update
sudo yum install -y fuse-devel
sudo mkfs -t ext4 /dev/xvdb
sudo mkdir /scratch_volume
sudo echo -e '/dev/xvdb\t/scratch_volume\text4\tdefaults\t0\t0' | sudo tee -a /etc/fstab
sudo mount –a
sudo stop ecs
sudo rm -rf /var/lib/ecs/data/ecs_agent_data.json
```
* save an image from the running EC2 instance

Once the AMI instance ID is available make sure you set the proper AMI instance in align/serverless.yml.
To deploy:
```
cd align
npm install
npm run sls -- deploy -s dev
```

The command above will create the compute environment, the job definition and the job queue.

### Deploy NeuronBridge¸ color depth search stack

In order to create the color depth search lambdas run:

```
cd search
npm install
npm run sls -- deploy -s dev
```

To deploy with different search limits:
```
PER_DAY_SEARCH_LIMITS=2 CONCURRENT_SEARCH_LIMITS=2 npm run sls -- deploy -s cgdev
```
Note: a negative value for a limit means unlimited.