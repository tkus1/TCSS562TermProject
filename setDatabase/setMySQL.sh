# installing the AWSCLI is optional
sudo apt update
sudo apt install awscli
aws configure
# provide ACCESS_KEY and SECRET_KEY
# Find your credentials on your existing VM with: “cat ~/.aws/credentials”
sudo apt update
# sudo apt install mysql-client-core-5.7 # For old-versions of Ubuntu < 20.04
sudo apt install mysql-client-core-8.0 # for Ubuntu >= 20.04

mysql --host=<Database endpoint> --port=3306 --enable-cleartext-plugin --user=tcss462_562 --password=<your database password>
