# Cloud Config 

## config-repo
Copy files to /home/config-repo/config-examples or change application.yml from config-server

### create local repo
~~~bash
# Linux
export TARGET=/home/config-repo/config-examples
# Windows with Git Bash, on drive 'e'
export TARGET=/e/home/config-repo/config-examples

mkdir -p $TARGET && cp config-repo/* $TARGET && cd $TARGET && git init && git add * && git commit -a -m init
~~~
