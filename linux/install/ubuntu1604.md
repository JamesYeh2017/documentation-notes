# Ubuntu 16.04


## apt 無法執行動作 (被lock住的解法)
- [更新套件被占用](http://hep1.phys.ntu.edu.tw/~phchen/apfel/linux/install/problem-solving.txt)
- 2018/04/23

更新套件庫或安裝套件遇到「無法將 /var/lib/dpkg/lock 鎖定」 解法
```sh
# 若出現 /var/lib/dpkg/lock 鎖定 - open (11: 資源暫時無法取得)
# 原因是使用 apt-get, aptitude, synaptic, software-center …等等的程式還沒有關閉

# 如果忘記是那個程式沒關的話，可使用 lsof(list open files) 找出是那個程序佔用檔案, 再用手動關閉或是使用指令的方法, 殺掉正在執行程序

# 1. 用 lsof 找出目前是那個程序在使用 /var/lib/dpkg/lock

$ sudo lsof /var/lib/dpkg/lock
# 從訊息可看出目前是 aptitude 在佔用 /var/lib/dpkg/lock, 可以找找看是不是剛使用 aptitude, 如果有的話等程式跑完應該就可 更新/安裝了

COMMAND    PID USER   FD   TYPE DEVICE SIZE/OFF    NODE NAME
aptitude 8891 root    4uW  REG   6,47        0 248214 /var/lib/dpkg/lock

# 2. 若找出來的程序是己經沒在執行又遺忘在那開啟的話, 直 kill~. 而這裡是 aptitude 它的 PID 為 8891, 殺掉就能正常使用

$ sudo kill 8891

# 如果出現
E: dpkg was interrupted, you must manually run 'sudo dpkg --configure -a' to correct the problem. 

# 就輸入
$ sudo dpkg --configure -a
```


# 無網路服務時, 安裝網路工具

- [Docker - Ubuntu - bash: ping: command not found](https://stackoverflow.com/questions/39901311/docker-ubuntu-bash-ping-command-not-found)

```sh
$# apt update
$# apt install iputils-ping
```



# install mysql 5.7

[MySQL官方教學](https://dev.mysql.com/doc/mysql-apt-repo-quick-guide/en/#apt-repo-fresh-install)

```sh
# 1. 連到這裡  https://dev.mysql.com/downloads/file/?id=472914
# 抓官方的repo

# 2.加入官方repo
$ sudo dpkg -i mysql-apt-config_0.8.8-1_all.deb

# 3. 更新一下apt
$ sudo apt-get -y update

# 4. 安裝mysql server
$ sudo apt-get -y install mysql-server

# 5. 服務
$ sudo service mysql status
$ sudo service mysql stop
$ sudo service mysql start
```



# MongoDB 3.4

- [官方教學](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/#install-mongodb-community-edition)

```sh
1. 加入官方public key
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 0C49F3730359A14518585931BC711F9BA15703C6

2. 建立16.04版的list file (這啥鬼...)
echo "deb [ arch=amd64,arm64 ] http://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/3.4 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.4.list

3. update
sudo apt-get update

4. 安裝
sudo sudo apt-get install -y mongodb
```



# Docker

- 2018/01/30
- [官方教學](https://docs.docker.com/install/linux/docker-ce/ubuntu/#os-requirements)

```sh
$ sudo apt-get update

$ sudo apt-get install docker-ce

$ apt-cache madison docker-ce
# 生產環境底下, 避免永遠都預設使用最新版

$ sudo apt-get install docker-ce=<VERSION>

$ sudo usermod -aG docker <userName>
# 使目前使用者能使用 Docker ((重新登入!!))
```



# vim (有顏色的 vi)

- 2018/01/30
- [Linux 安裝 vim](https://www.phpini.com/linux/linux-install-vim)
- [How to change syntax color in vim?](https://askubuntu.com/questions/912404/how-to-change-syntax-color-in-vim)
- [Backspace in insert mode in vi doesn't erase the character](https://askubuntu.com/questions/296385/backspace-in-insert-mode-in-vi-doesnt-erase-the-character)

```sh
$ sudo apt -y install vim

$ ls /usr/share/vim/vim74/colors
blue.vim  default.vim  desert.vim  evening.vim  koehler.vim  ...等 19 種
# 可選擇自己喜歡的顏色風格

$ vi ~/.vimrc
colorscheme koehler
```


## 或者可以使用預設的 vi, 取消 Ubuntu 難用的兼容模式...

```vim
# .vimrc
set nocompatible
set backspace=2
```



# 安裝 git

- 2018/06/19
- [git 官方查看自己要的版本 (Branch: master那邊)](https://github.com/git/git)

> Docker Image : ubuntu:16.04, 安裝 `非 apt預設版本的 git`

```sh
# 進入 Docker Container 後
$ apt update
$ apt install -y git
$ git --version
git version 2.7.4
# ↑ 2018/06 都已經出到 2.18rc 了, 2.7版 有點老舊... 

$ apt remove -y git
# 所以把它砍了, 自己來編譯~~~
```

所以改抓 `tarball` 自行編譯

```sh
$ cd /opt
$ apt -y update
$ apt -y install wget
$ wget https://github.com/git/git/archive/v2.14.4.tar.gz

# 編譯時會用到的東西 (200多 MB  公司網路速度超慢... ㄇㄉ~)
$ apt install -y make autoconf gcc zlib1g-dev tcl-dev libssl-dev gettext

$ tar zxf v2.14.4.tar.gz
$ cd git-2.14.4/

$ make configure
GIT_VERSION = 2.14.4
    GEN configure

$ ./configure --prefix=/usr/local
# 注意看看有沒有 error 等錯誤字眼

$ make
$ make prefix=/usr/local install
$ git --version
git version 2.14.4  # 成功!

# 完成後
$ cd ..
$ rm v2.14.4.tar.gz
```

# Install python3
- 2018/06/19
- [How do I install Python 3.6 using apt-get?](https://askubuntu.com/questions/865554/how-do-i-install-python-3-6-using-apt-get)

> Docker Image : ubuntu:16.04(預設裏頭沒有 python), 自行安裝 `python3`

```sh
# 同上範例的 git, 安裝 gcc, zlib1g-dev 那堆編譯的東西...

$ wget https://www.python.org/ftp/python/3.6.1/Python-3.6.1.tgz
$ tar zxf Python-3.6.1.tgz
$ cd Python-3.6.1

$ ./configure --enable-optimizations
$ make # 要等一陣子~~
$ make install  # 乾~~ 這個等更久

$ python3 --version
Python 3.6.1

$ which python3
/usr/local/bin/python3

$ which pip3
/usr/local/bin/pip3
```

# SSH Server

- 2018/11/28

```sh
apt install -y openssh-server
```


# certbot

- 2018/11/28
- letsencrypt

1. 安裝
```sh
$# apt-get -y install software-properties-common
$# add-apt-repository ppa:certbot/certbot
$# apt-get -y update
$# apt-get -y install certbot
```

2. 設定檔
```sh
# cli.ini
$# vim cli.ini
text = True
domains = YOURDOMAIN.com
email = MAIL@YOURMAIL
renew-by-default
agree-tos
rsa-key-size = 4096
logs-dir = /tmp/certbot/logs/
work-dir = /tmp/certbot/work/
```

3. 使用
```sh
$# certbot certonly -a manual -c cli.ini
# 然後就去加這個了 「/.well-known/acme-challenge/@^(^*@%#^*@%$*@#&...」

```