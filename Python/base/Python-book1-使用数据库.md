
# 第七章 使用数据库

## Python 安装 Mariadb 驱动

###  Mac 环境

- [How to connect Python programs to MariaDB](https://mariadb.com/resources/blog/how-to-connect-python-programs-to-mariadb/)
- [installing RMySQL package on Mac Catallina](https://stackoverflow.com/questions/60881084/installing-rmysql-package-on-mac-catallina)

```bash
brew install mariadb-connector-c
python3 -m pip install mariadb
```

### Linux 环境 (未验证)

- [Problem with pip install mariadb - mariadb_config not found](https://stackoverflow.com/questions/63027020/problem-with-pip-install-mariadb-mariadb-config-not-found)

```bash
sudo apt-get install -y libmariadb-dev
python3 -m pip install mariadb
```


## Python 安装 MySQL 驱动 (未验证)

### Linux 环境 (未验证)

- [pip install mysql-python fails with EnvironmentError: mysql_config not found](https://stackoverflow.com/questions/5178292/pip-install-mysql-python-fails-with-environmenterror-mysql-config-not-found)

```bash
sudo apt-get install libmysqlclient-dev
python3 -m pip install mysql-python
```

--

## 创建测试表

```sql
CREATE DATABASE vsearchlogDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE vsearchlogDB;
CREATE TABLE log (
    id int auto_increment primary key,
    ts timestamp default current_timestamp,
    phrase varchar(128) not null,
    letters varchar(32) not null,
    ip varchar(16) not null,
    browser_string varchar(256) not null,
    results varchar(64) not null
);
DESC log;
```

## 记录

## Python 安装 Mariadb 驱动 - 工作外网机 Mac 环境

```bash
yhdeiMac:tmp yh$ brew install mariadb-connector-c
Updating Homebrew...
==> Downloading https://homebrew.bintray.com/bottles-portable-ruby/portable-ruby-2.6.3_2.yosemite.bottle.tar.gz
######################################################################## 100.0%
==> Pouring portable-ruby-2.6.3_2.yosemite.bottle.tar.gz
==> Auto-updated Homebrew!
Updated 2 taps (homebrew/core and denisidoro/tools).
==> New Formulae
acl2                           flarectl                       libolm                         protoc-gen-gogofaster
act                            fleet-cli                      liboqs                         pwncat
airshare                       flit                           libpciaccess                   python@3.7
alsa-lib                       flux                           libpqxx@6                      python@3.9
amp                            fnm                            libpthread-stubs               qrcp
apidoc                         folderify                      libseccomp                     rain
arb                            font-util                      libslirp                       rbtools
argo                           foreman                        libsm                          redo
argocd                         fpart                          libtorrent-rakshasa            reg
arrayfire                      fpdns                          libx11                         regipy
arturo                         fplll                          libxau                         reorder-python-imports
asimov                         functionalplus                 libxaw                         rgf
asroute                        gateway-go                     libxaw3d                       rm-improved
asuka                          gau                            libxcb                         rqlite
athenacli                      gcalcli                        libxcomposite                  rtorrent
austin                         gcc@9                          libxcursor                     rust-analyzer
awsweeper                      ghc@8.8                        libxdamage                     rustscan
bit-git                        ghz                            libxdmcp                       s2n
blaze                          ghz-web                        libxext                        saltwater
blogc                          git-hooks-go                   libxfixes                      sdns
bombadillo                     git-hound                      libxfont                       semgrep
bond                           gitlint                        libxft                         server-go
borgbackup                     gitql                          libxi                          shallow-backup
box2d                          gitui                          libxinerama                    sheldon
buildozer                      glab                           libxkbfile                     showkey
c7n                            gluon                          libxmu                         shtools
cadence                        go@1.14                        libxp                          silicon
carton                         gocloc                         libxpm                         simdjson
cassowary                      gofish                         libxrandr                      skylighting
castget                        golangci-lint                  libxrender                     sleef
cbc                            googletest                     libxres                        smlpkg
cddlib                         gosec                          libxscrnsaver                  snap
cdktf                          gost                           libxshmfence                   so
cdo                            gostatic                       libxt                          solidity
cfn-flip                       gradle-profiler                libxtst                        sollya
cgl                            graphql-cli                    libxv                          sonic
chalk-cli                      gravity                        libxvmc                        sponge
charge                         grpcui                         libxxf86dga                    spotify-tui
chars                          guile@2                        libxxf86vm                     spotifyd
chart-testing                  gulp-cli                       linux-headers                  sqlite-utils
checkov                        halide                         litecli                        standardese
choose-rust                    hashlink                       lizard-analyzer                staticcheck
chrony                         hasura-cli                     localstack                     strace
clair                          hdf5-mpi                       logcli                         structurizr-cli
clang-format@8                 heksa                          loki                           subfinder
claws-mail                     httpx                          lunchy                         taskwarrior-tui
cli11                          hy                             lunchy-go                      termcolor
clip                           i686-elf-binutils              macos-trash                    terraform-ls
cloud-nuke                     i686-elf-gcc                   mandown                        terraform@0.12
cloudformation-cli             idris2                         mariadb@10.4                   terrascan
cloudformation-guard           immudb                         markdownlint-cli               tfsec
coconut                        infracost                      marked                         thanos
code-server                    inja                           mask                           toot
colfer                         inko                           matplotplusplus                torchvision
commitizen                     ioctl                          mesa-glu                       trailscraper
condure                        isl@0.18                       mhonarc                        tre-command
copilot                        isort                          microplane                     tree-sitter
coredns                        jerryscript                    naabu                          trunk
cortex                         jimtcl                         nanorc                         ugrep
cpio                           jinx                           ncspot                         unum
cpm                            jobber                         nest                           uptoc
cpr                            jsonnet-bundler                networkit                      usb.ids
cqlkit                         k3sup                          never                          util-macros
croaring                       k9s                            nfpm                           uutils-coreutils
croc                           kamel                          ngs                            vapor
cubejs-cli                     kde-extra-cmake-modules        nicotine-plus                  vcpkg
cucumber-ruby                  kde-karchive                   node@14                        vgrep
cvs-fast-export                kde-kdoctools                  notmuch-mutt                   vint
datasette                      kde-ki18n                      numcpp                         vivid
dbdeployer                     kde-threadweaver               oci-cli                        vlang
denisidoro/tools/docpars       kona                           omake                          vlmcsd
device-mapper                  kondo                          openfast                       volk
dgraph                         ksync                          openfst                        vsearch
diskonaut                      kube-linter                    openjdk@8                      vtk@8.2
dmagnetic                      kubie                          openstackclient                vulture
dnsprobe                       kumactl                        oq                             wangle
doctest                        lab                            or-tools                       wasm-pack
dog                            ladspa-sdk                     ormolu                         webify
dosbox-staging                 lanraragi                      ory-hydra                      wgcf
dotenv-linter                  latexindent                    osi                            wownero
dotnet                         lc0                            osm                            wren
duckdb                         ldpl                           overdrive                      wren-cli
duckscript                     leaf                           ox                             x86_64-elf-gdb
duktape                        leakcanary-shark               packetbeat                     xbitmaps
earthly                        libaio                         packr                          xcb-proto
easy-rsa                       libcap                         pandoc-include-code            xcb-util
eksctl                         libcouchbase@2                 pandocomatic                   xdpyinfo
eleventy                       libdmx                         parallel-hashmap               xkeyboardconfig
empty                          libdrm                         pdm                            xorgproto
envoy                          libfontenc                     periscope                      xorgrgb
erlang@22                      libfs                          pfetch                         xterm
eva                            libfuse                        phive                          xtrans
fargatecli                     libgccjit                      pickle                         xxh
fava                           libgnt                         pipgrip                        yh
fblog                          libhandy                       po4a                           yj
fennel                         libice                         podman                         z.lua
fetch                          libirecovery                   postgresql@12                  zenith
field3d                        libmnl                         prometheus-cpp                 zoxide
fizz                           libnetfilter-queue             promtail                       zsh-you-should-use
flamegraph                     libnetworkit                   protoc-gen-go-grpc
flank                          libnfnetlink                   protoc-gen-gogo
==> Updated Formulae
Updated 4777 formulae.
==> Renamed Formulae
elasticsearch@6.8 -> elasticsearch@6                           jfrog-cli-go -> jfrog-cli
gst-validate -> gst-devtools                                   kibana@6.8 -> kibana@6
interactive-rebase-tool -> git-interactive-rebase-tool         mkl-dnn -> onednn
==> Deleted Formulae
baidupcs-go              cryptopp                 highlighting-kate        mysql-connector-c++@1.1  tomee-jax-rs
biogeme                  deis                     i386-elf-grub            ori                      unravel
boost@1.55               deisctl                  kibana@5.6               pgplot                   urbit
boost@1.59               elasticsearch@2.4        llvm@6                   pijul                    woboq_codebrowser
cargo-completion         elasticsearch@5.6        lumo                     python                   wpscan
confluent-platform       fmsx                     marathon-swift           residualvm               xu4
crc                      gnome-builder            meson-internal           sflowtool

==> Downloading https://homebrew.bintray.com/bottles/openssl%401.1-1.1.1h.mojave.bottle.tar.gz
==> Downloading from https://d29vzk4ow07wi7.cloudfront.net/d4ef27b41d0596d20b79a43a43554d4ea1395f0ef9affdcf0ce74114a00e2572?r
######################################################################## 100.0%
==> Downloading https://homebrew.bintray.com/bottles/mariadb-connector-c-3.1.11.mojave.bottle.tar.gz
==> Downloading from https://d29vzk4ow07wi7.cloudfront.net/d49661c28ad85799cc966b04fff2722084ee307115c77028a93521eaa6e28643?r
######################################################################## 100.0%
==> Installing dependencies for mariadb-connector-c: openssl@1.1
==> Installing mariadb-connector-c dependency: openssl@1.1
==> Pouring openssl@1.1-1.1.1h.mojave.bottle.tar.gz
==> Caveats
A CA file has been bootstrapped using certificates from the system
keychain. To add additional certificates, place .pem files in
  /usr/local/etc/openssl@1.1/certs

and run
  /usr/local/opt/openssl@1.1/bin/c_rehash

openssl@1.1 is keg-only, which means it was not symlinked into /usr/local,
because macOS provides LibreSSL.

If you need to have openssl@1.1 first in your PATH run:
  echo 'export PATH="/usr/local/opt/openssl@1.1/bin:$PATH"' >> /Users/yh/.bash_profile

For compilers to find openssl@1.1 you may need to set:
  export LDFLAGS="-L/usr/local/opt/openssl@1.1/lib"
  export CPPFLAGS="-I/usr/local/opt/openssl@1.1/include"

==> Summary
🍺  /usr/local/Cellar/openssl@1.1/1.1.1h: 8,067 files, 18.4MB
==> Installing mariadb-connector-c
==> Pouring mariadb-connector-c-3.1.11.mojave.bottle.tar.gz
🍺  /usr/local/Cellar/mariadb-connector-c/3.1.11: 32 files, 1MB
==> `brew cleanup` has not been run in 30 days, running now...
Removing: /Users/yh/Library/Caches/Homebrew/cheat--3.9.0.mojave.bottle.tar.gz... (3.6MB)
Removing: /Users/yh/Library/Caches/Homebrew/fzf--0.21.1.mojave.bottle.tar.gz... (2.0MB)
Removing: /Users/yh/Library/Caches/Homebrew/navi--2.4.1.tar.gz... (2.0MB)
Removing: /Users/yh/Library/Caches/Homebrew/portable-ruby-2.6.3.mavericks.bottle.tar.gz... (9.0MB)
Removing: /Users/yh/Library/Logs/Homebrew/navi... (117B)
Removing: /Users/yh/Library/Logs/Homebrew/cheat... (64B)
Removing: /Users/yh/Library/Logs/Homebrew/fzf... (64B)
Pruned 1 symbolic links and 2 directories from /usr/local
==> Caveats
==> openssl@1.1
A CA file has been bootstrapped using certificates from the system
keychain. To add additional certificates, place .pem files in
  /usr/local/etc/openssl@1.1/certs

and run
  /usr/local/opt/openssl@1.1/bin/c_rehash

openssl@1.1 is keg-only, which means it was not symlinked into /usr/local,
because macOS provides LibreSSL.

If you need to have openssl@1.1 first in your PATH run:
  echo 'export PATH="/usr/local/opt/openssl@1.1/bin:$PATH"' >> /Users/yh/.bash_profile

For compilers to find openssl@1.1 you may need to set:
  export LDFLAGS="-L/usr/local/opt/openssl@1.1/lib"
  export CPPFLAGS="-I/usr/local/opt/openssl@1.1/include"

yhdeiMac:tmp yh$ 
yhdeiMac:tmp yh$ 
yhdeiMac:tmp yh$ 
yhdeiMac:tmp yh$ 
yhdeiMac:tmp yh$ python3 -m pip install mariadb
Collecting mariadb
  Using cached mariadb-1.0.4.tar.gz (66 kB)
Using legacy 'setup.py install' for mariadb, since package 'wheel' is not installed.
Installing collected packages: mariadb
    Running setup.py install for mariadb ... done
Successfully installed mariadb-1.0.4
WARNING: You are using pip version 20.2.3; however, version 20.2.4 is available.
You should consider upgrading via the '/Library/Frameworks/Python.framework/Versions/3.9/bin/python3 -m pip install --upgrade pip' command.
yhdeiMac:tmp yh$ 
```
