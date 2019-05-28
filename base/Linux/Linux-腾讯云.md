
# 腾讯云 centos7 重启后无法登陆的问题

- [腾讯云 centos7 重启后无法登陆的问题](https://cloud.tencent.com/developer/article/1368176)

```bash
➜  ~ ssh root@193.112.38.200
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@    WARNING: REMOTE HOST IDENTIFICATION HAS CHANGED!     @
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
IT IS POSSIBLE THAT SOMEONE IS DOING SOMETHING NASTY!
Someone could be eavesdropping on you right now (man-in-the-middle attack)!
It is also possible that a host key has just been changed.
The fingerprint for the RSA key sent by the remote host is
d5:b1:72:c5:3f:e7:84:8f:51:cb:05:63:4d:92:15:20.
Please contact your system administrator.
Add correct host key in /Users/user/.ssh/known_hosts to get rid of this message.
Offending RSA key in /Users/user/.ssh/known_hosts:3
RSA host key for 193.112.38.200 has changed and you have requested strict checking.
Host key verification failed.
➜  ~ more /Users/user/.ssh/known_hosts
123.207.120.205 ssh-rsa AAAAB3tg7KNfhmYiJBTt4fBD6vkmi7GujfgBcoxKFy5fdtvFcsivv6FfiDYG8q2BcuOfBV13Me2Z2piuEdfdfcvccxzcvc
193.112.38.200 ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDQiMoWXlg/oiN7P5iFAx+3vdVLk8Gq24TPjUDowP22D0CZxxxxxxxxxsxdsrsdfdffccccswe
```

