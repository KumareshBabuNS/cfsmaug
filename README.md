# IMPORTANT

THIS IS A FORK WITH MASSIVE REFACTORING TO SPRING BOOT and extra features

We did a fork to link to the original project but we don't expect to do pull request or merge.

The project is very unstable and has no release yet.

All documentation below does not apply
Many files will be deleted incrementally.


July 2016

## How to run in CF

You need to create a user provided service named `smaug-api`. Please refer to the `deploy` folder.
Note that env vars (see below) will NOT override the cups when cloud profile is detected.
 
## How to run standalone or from source


You may need to add `cf-env` to your local maven repository. Please explore the `lib` folder.

You need the below properties. Use Spring boot properties different files or env as you see fit.
For Eclipse and dev tools, use secret as spring active profile and add an `application-secret.yml` in the project folder.
We encourage to do that as `.gitignore` will ignore the `application-secret.yml`

On command line
```
./mvnw clean package -DskipTests
java -Dspring.profile.active=secret ...
```

Example `application-secret.yml`
```
---
cf:
  host: api.run.smaugsystemdomain
  username: admin
  password: xxx
```

You should use uaac to create an admin user instead of use the admin user from your install, so that you can rotate passwords.





# admin-portal

### this is an admin portal to show some metrics about your foundation.

## Deploying a release binary to cloud foundry

```
# go to:
# https://github.com/pivotal-pez/admin-portal/releases/latest 
# and download adminportal.tgz

$ tar -xvzf adminportal.tgz

$ cd adminportal

$ cf login -a [cf.api.domain] -u [cfadminuser] -p [adminuserpass] -o
[mytargetorg] -s [mytargetspace] --skip-ssl-validation

# this will setup a user provided service containing foundation api url and user
information.
# the user will need to have uaa.admin and cloudcontroller.admin priviledges.
$ cat cups.txt | sh 

$ cf push adminportal

```


## Running locally for development

```

# install the wercker cli
$ curl -L https://install.wercker.com | sh

#copy the sample env config file
$ cp myenv.example myenv # fill in your details in the newly created myenv file

#copy the sample wercker local deployment manifest
$ cp wercker_local_deploy.example wercker_local_deploy.yml # fill in your details in the newly created file


#copy the sample vcap_services definition
$ cp vcap_services_template.json.example vcap_services_template.json # fill in your details in the newly created file



# make sure a docker host is running
$ boot2docker up && $(boot2docker shellinit)

# run the app locally using wercker magic
$ ./runlocaldeploy myenv

$ echo "open ${DOCKER_HOST} in your browser to view this app locally"

```
