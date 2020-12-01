
# xxmac Python 环境

```bash
xxmac:~ yh$ echo $PATH
/Library/Frameworks/Python.framework/Versions/3.9/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:/Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home/bin:/Volumes/files/zz/app/scala/scala-2.12.11/bin

xxmac:~ yh$ 
xxmac:~ yh$ ls -l /System/Library/Frameworks/Python.framework/Versions/
total 0
lrwxr-xr-x   1 root  wheel    3 Dec 25  2019 2.3 -> 2.7
lrwxr-xr-x   1 root  wheel    3 Dec 25  2019 2.5 -> 2.7
lrwxr-xr-x   1 root  wheel    3 Dec 25  2019 2.6 -> 2.7
drwxr-xr-x  10 root  wheel  320 Sep 21  2019 2.7
lrwxr-xr-x   1 root  wheel    3 Dec 25  2019 Current -> 2.7
xxmac:~ yh$ ls -l /System/Library/Frameworks/Python.framework/Versions/2.7/bin/
total 48
lrwxr-xr-x  1 root  wheel      7 Dec 25  2019 2to3 -> 2to32.7
lrwxr-xr-x  1 root  wheel      8 Dec 25  2019 2to3-2 -> 2to3-2.7
-rwxr-xr-x  1 root  wheel    288 Feb 23  2019 2to3-2.7
lrwxr-xr-x  1 root  wheel      6 Dec 25  2019 2to32.7 -> 2to3-2
lrwxr-xr-x  1 root  wheel      5 Dec 25  2019 idle -> idle2
lrwxr-xr-x  1 root  wheel      7 Dec 25  2019 idle2 -> idle2.7
-rwxr-xr-x  1 root  wheel    230 Feb 23  2019 idle2.7
lrwxr-xr-x  1 root  wheel      6 Dec 25  2019 pydoc -> pydoc2
lrwxr-xr-x  1 root  wheel      8 Dec 25  2019 pydoc2 -> pydoc2.7
-rwxr-xr-x  1 root  wheel    215 Feb 23  2019 pydoc2.7
lrwxr-xr-x  1 root  wheel      7 Dec 25  2019 python -> python2
lrwxr-xr-x  1 root  wheel     14 Dec 25  2019 python-config -> python2-config
lrwxr-xr-x  1 root  wheel      9 Dec 25  2019 python2 -> python2.7
lrwxr-xr-x  1 root  wheel     16 Dec 25  2019 python2-config -> python2.7-config
-rwxr-xr-x  1 root  wheel  43104 Sep 21  2019 python2.7
-rwxr-xr-x  1 root  wheel   1842 Feb 23  2019 python2.7-config
lrwxr-xr-x  1 root  wheel      8 Dec 25  2019 pythonw -> pythonw2
lrwxr-xr-x  1 root  wheel     10 Dec 25  2019 pythonw2 -> pythonw2.7
-rwxr-xr-x  1 root  wheel  43104 Sep 21  2019 pythonw2.7
lrwxr-xr-x  1 root  wheel      9 Dec 25  2019 smtpd.py -> smtpd2.py
-rwxr-xr-x  1 root  wheel  18681 Feb 23  2019 smtpd2.7.py
lrwxr-xr-x  1 root  wheel     11 Dec 25  2019 smtpd2.py -> smtpd2.7.py
xxmac:~ yh$ 
xxmac:~ yh$ ls -l /Library/Frameworks/Python.framework/Versions/
total 0
drwxrwxr-x  12 root  admin  384 Nov 16 09:23 3.9
lrwxr-xr-x   1 root  wheel    3 Nov 14 16:57 Current -> 3.9
xxmac:~ yh$ 
xxmac:~ yh$ ls -l /Library/Frameworks/Python.framework/Versions/3.9/bin/
total 152
lrwxr-xr-x  1 root  admin      8 Nov 14 16:57 2to3 -> 2to3-3.9
-rwxrwxr-x  1 root  admin    140 Oct  5 23:29 2to3-3.9
-rwxrwxr-x  1 root  admin    277 Nov 14 16:57 easy_install-3.9
-rwxr-xr-x  1 yh    admin    253 Nov 16 11:47 flask
lrwxr-xr-x  1 root  admin      7 Nov 14 16:57 idle3 -> idle3.9
-rwxrwxr-x  1 root  admin    138 Oct  5 23:29 idle3.9
-rwxr-xr-x  1 yh    admin    250 Nov 14 17:19 pep8
-rwxrwxr-x  1 root  admin    268 Nov 14 16:57 pip3
-rwxrwxr-x  1 root  admin    268 Nov 14 16:57 pip3.9
-rwxr-xr-x  1 yh    admin    266 Nov 16 11:32 py.test
lrwxr-xr-x  1 root  admin      8 Nov 14 16:57 pydoc3 -> pydoc3.9
-rwxrwxr-x  1 root  admin    123 Oct  5 23:29 pydoc3.9
-rwxr-xr-x  1 yh    admin    266 Nov 16 11:32 pytest
lrwxr-xr-x  1 root  admin      9 Nov 14 16:57 python3 -> python3.9
lrwxr-xr-x  1 root  admin     16 Nov 14 16:57 python3-config -> python3.9-config
-rwxrwxr-x  1 root  admin  29008 Oct  5 23:36 python3.9
-rwxrwxr-x  1 root  admin   2082 Oct  5 23:29 python3.9-config
xxmac:~ yh$ 

xxmac:~ yh$ python --version
Python 2.7.10
xxmac:~ yh$ python3 --version
Python 3.9.0
xxmac:~ yh$ whereis python
/usr/bin/python
xxmac:~ yh$ whereis python3
xxmac:~ yh$ 

xxmac:~ yh$ ls -l /usr/bin/py*
-rwxr-xr-x  4 root  wheel    925 Feb 23  2019 /usr/bin/pydoc
lrwxr-xr-x  1 root  wheel     74 Dec 25  2019 /usr/bin/pydoc2.7 -> ../../System/Library/Frameworks/Python.framework/Versions/2.7/bin/pydoc2.7
-rwxr-xr-x  1 root  wheel  66880 Sep 21  2019 /usr/bin/python
-rwxr-xr-x  4 root  wheel    925 Feb 23  2019 /usr/bin/python-config
lrwxr-xr-x  1 root  wheel     75 Dec 25  2019 /usr/bin/python2.7 -> ../../System/Library/Frameworks/Python.framework/Versions/2.7/bin/python2.7
lrwxr-xr-x  1 root  wheel     82 Dec 25  2019 /usr/bin/python2.7-config -> ../../System/Library/Frameworks/Python.framework/Versions/2.7/bin/python2.7-config
-rwxr-xr-x  1 root  wheel  66880 Sep 21  2019 /usr/bin/pythonw
lrwxr-xr-x  1 root  wheel     76 Dec 25  2019 /usr/bin/pythonw2.7 -> ../../System/Library/Frameworks/Python.framework/Versions/2.7/bin/pythonw2.7
xxmac:~ yh$ 
xxmac:~ yh$ ls -l /Library/Frameworks/Python.framework/Versions/3.9/bin/py*
-rwxr-xr-x  1 yh    admin    266 Nov 16 11:32 /Library/Frameworks/Python.framework/Versions/3.9/bin/py.test
lrwxr-xr-x  1 root  admin      8 Nov 14 16:57 /Library/Frameworks/Python.framework/Versions/3.9/bin/pydoc3 -> pydoc3.9
-rwxrwxr-x  1 root  admin    123 Oct  5 23:29 /Library/Frameworks/Python.framework/Versions/3.9/bin/pydoc3.9
-rwxr-xr-x  1 yh    admin    266 Nov 16 11:32 /Library/Frameworks/Python.framework/Versions/3.9/bin/pytest
lrwxr-xr-x  1 root  admin      9 Nov 14 16:57 /Library/Frameworks/Python.framework/Versions/3.9/bin/python3 -> python3.9
lrwxr-xr-x  1 root  admin     16 Nov 14 16:57 /Library/Frameworks/Python.framework/Versions/3.9/bin/python3-config -> python3.9-config
-rwxrwxr-x  1 root  admin  29008 Oct  5 23:36 /Library/Frameworks/Python.framework/Versions/3.9/bin/python3.9
-rwxrwxr-x  1 root  admin   2082 Oct  5 23:29 /Library/Frameworks/Python.framework/Versions/3.9/bin/python3.9-config
xxmac:~ yh$ 
xxmac:~ yh$ ls -l /usr/local/bin/py*
lrwxr-xr-x  1 root  wheel  68 Nov 14 16:57 /usr/local/bin/pydoc3 -> ../../../Library/Frameworks/Python.framework/Versions/3.9/bin/pydoc3
lrwxr-xr-x  1 root  wheel  70 Nov 14 16:57 /usr/local/bin/pydoc3.9 -> ../../../Library/Frameworks/Python.framework/Versions/3.9/bin/pydoc3.9
lrwxr-xr-x  1 root  wheel  69 Nov 14 16:57 /usr/local/bin/python3 -> ../../../Library/Frameworks/Python.framework/Versions/3.9/bin/python3
lrwxr-xr-x  1 root  wheel  76 Nov 14 16:57 /usr/local/bin/python3-config -> ../../../Library/Frameworks/Python.framework/Versions/3.9/bin/python3-config
lrwxr-xr-x  1 root  wheel  71 Nov 14 16:57 /usr/local/bin/python3.9 -> ../../../Library/Frameworks/Python.framework/Versions/3.9/bin/python3.9
lrwxr-xr-x  1 root  wheel  78 Nov 14 16:57 /usr/local/bin/python3.9-config -> ../../../Library/Frameworks/Python.framework/Versions/3.9/bin/python3.9-config
xxmac:~ yh$ 

xxmac:~ yh$ 
xxmac:~ yh$ cat ~/.bash_profile
# java
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home
export PATH=$PATH:$JAVA_HOME/bin

# scala
export SCALA_HOME=/Volumes/files/zz/app/scala/scala-2.12.11
export PATH=$PATH:$SCALA_HOME/bin

# Setting PATH for Python 3.9
# The original version is saved in .bash_profile.pysave
PATH="/Library/Frameworks/Python.framework/Versions/3.9/bin:${PATH}"
export PATH
xxmac:~ yh$ 

xxmac:~ yh$ whereis idle
/usr/bin/idle
xxmac:~ yh$ whereis idle3
xxmac:~ yh$ 

xxmac:~ yh$ ls -l /usr/bin/idle*
-rwxr-xr-x  4 root  wheel  925 Feb 23  2019 /usr/bin/idle
lrwxr-xr-x  1 root  wheel   73 Dec 25  2019 /usr/bin/idle2.7 -> ../../System/Library/Frameworks/Python.framework/Versions/2.7/bin/idle2.7
xxmac:~ yh$ 
xxmac:~ yh$ ls -l /Library/Frameworks/Python.framework/Versions/3.9/bin/idle*
lrwxr-xr-x  1 root  admin    7 Nov 14 16:57 /Library/Frameworks/Python.framework/Versions/3.9/bin/idle3 -> idle3.9
-rwxrwxr-x  1 root  admin  138 Oct  5 23:29 /Library/Frameworks/Python.framework/Versions/3.9/bin/idle3.9
xxmac:~ yh$ 
```
