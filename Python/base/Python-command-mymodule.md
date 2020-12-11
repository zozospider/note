
# Python 安装模块到 site-pachages

## 1. 准备好文件

- file-1. 模块文件: `mymodules/vsearch/vsearch.py`

```python
def search_for_vowels(phrase:str) -> str:
    """Return any vowels found in a supplied phrase."""
    vowels = set('aeiou')
    return vowels.intersection(set(phrase))

def search_for_letters(phrase:str, letters:str='aeiou') -> set:
    """Return a set of the 'letters' found in 'phrase'."""
    return set(letters).intersection(set(phrase))
```

- file-2. 发布文件: `mymodules/vsearch/setup.py`

```python
from setuptools import setup

setup(
    name='vsearch',
    version='1.0',
    description='The Head First Python Search Tools',
    author='HF Python 2e',
    author_email='hfpy2e@gmail.com',
    url='headfirstlabs.com',
    py_modules=['vsearch']
)
```

- file-3. `README` 文件: `mymodules/vsearch/README.txt`

empty

## 2. 发布文件

```bash
yhdeiMac:vsearch yh$ pwd
/Volumes/files/zz/code/python_work_head_first/mymodules/vsearch
yhdeiMac:vsearch yh$ python3 setup.py sdist
```

## 3. 安装发布文件

```bash
yhdeiMac:dist yh$ pwd
/Volumes/files/zz/code/python_work_head_first/mymodules/vsearch/dist
yhdeiMac:dist yh$ python3 -m pip install --user vsearch-1.0.tar.gz
```

## 4. 使用

```python
import vsearch
```

```python
from vsearch import search_for_letters
```

---
