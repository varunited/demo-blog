dbpass=password
mysqlver=5.6

apt-get -y remove mysql-server mysql-client mysql-common
apt-get -y autoremove
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 8507EFA5

add-apt-repository 'deb http://repo.percona.com/apt trusty main'
add-apt-repository 'deb-src http://repo.percona.com/apt trusty main'

touch /etc/apt/preferences.d/00percona.pref
echo "Package: *
Pin: release o=Percona Development Team
Pin-Priority: 1001" > /etc/apt/preferences.d/00percona.pref

export DEBIAN_FRONTEND=noninteractive
echo "percona-server-server percona-server-server/root_password password $dbpass" | debconf-set-selections
echo "percona-server-server percona-server-server/root_password_again password $dbpass" | debconf-set-selections

apt-get update
apt-get -y install percona-server-server-$mysqlver

mysql -u root -p$dbpass <<EOF
create database demo_blog;
GRANT ALL ON *.* to demo_blog@localhost IDENTIFIED BY '$dbpass';
GRANT ALL ON *.* to demo_blog@'%' IDENTIFIED BY '$dbpass';
EOF

if [ "$mysqlver" == "5.6" ]
then
        sed -i -- 's/^bind-address/#bind-address/' /etc/mysql/my.cnf
else
        sed -i -- 's/^bind-address/#bind-address/' /etc/mysql/percona-server.conf.d/mysqld.cnf
fi

service mysql restart
