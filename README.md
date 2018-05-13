# FoolEdit

__The editor for fool__

# 傻瓜编辑器

__全心全意为傻瓜服务__

## 哲学

在总结各种编辑器兴衰成败规律的基础上，在软件工艺指导思想的引领下，我们确立了有傻瓜特色软件开发的思路和战略：

### 第一要义是发展

__我们仅仅知道唯一的一门科学，即历史科学。__

我们应当各种编辑器中吸取血的经验教训：
- Emacs类编辑器具备强大的可定制性，首次使用体验一般，一些功能不易发现
- Vi类编辑器的按键组合虽然让老手达到惊人的效率-光标可跟上眼球随处移动，但vi的模式在一开始就吓退了许多潜在用户
- Pico类编辑器功能比较少，但由于每个新手第一次打开它就知道怎么用且难以崩溃，所以Debian把Nano作为默认编辑器
- gedit类编辑器功能不强使用也较容易，另外对大文件支持不好
- jEdit具备强大的功能和可扩展性，可惜不太顺手
- IntelliJ和Netbeans之类IDE的编辑器自动补全、修正建议、重构相当实用，但某些基本编辑功能并不齐全，而且支持的语言比较少

### 核心是以人为本

__文明就是通过让人们能不动脑子地完成越来越多工作而进步的__

我们坚信应该由软件迎合人而不能让人迎合软件。

### 基本要求是全面协调可持续

通过良好的模块化，力保不同部分可以分别发展，形成健康的生态系统。

### 根本方法是统筹兼顾

- 多而粗与小而精相结合
- 让一部分功能先好起来与带动其它功能好起来相结合
- 先污染与后治理相结合
- 大力引进现成的库与形成傻瓜特色相结合

## 进度

** 傻瓜编辑器预期在2018年第三季开始公测，在此之前API和所有数据文件格式完全不考虑向后兼容性，稳定性、响应速度和错误处理也不在首要考虑，故切勿用于严肃的场合，为防误用，暂时不会公开完整构建脚本或二进制文件 **

### 支持的文件种类

#### 二进制文件

现已支持以十六进制方式读写二进制文件，但我们希望未来加入企图解读字节和按语法解析二进制文件的功能。

#### 文本文件

文本文件的高效编辑要求：
- 快速选中待编辑处
- 强力编辑

##### 语言支持情况

- ada支持词法加亮
- awk支持词法加亮
- bash支持词法加亮
- bibtex支持词法加亮
- c支持词法加亮、语法检查
- cobol支持词法加亮、语法检查
- C++支持词法加亮、语法检查
- csharp支持词法加亮、语法检查
- css支持词法加亮、语法检查
- csv支持词法加亮、语法检查
- diff支持词法加亮
- dot支持词法加亮、语法检查
- dtd支持词法加亮、语法检查
- elisp支持词法加亮
- erlang支持词法加亮、语法检查
- fortran支持词法加亮、语法检查
- gettext支持词法加亮
- go支持词法加亮、语法检查
- haskell支持词法加亮
- java支持词法加亮、语法检查
- javascript支持词法加亮、语法检查
- json支持词法加亮、语法检查
- lex支持词法加亮
- lua支持词法加亮、语法检查
- m4支持词法加亮
- makefile支持词法加亮
- markdown支持词法加亮
- Objective C支持词法加亮、语法检查
- ocaml支持词法加亮
- octave支持词法加亮
- pascal支持词法加亮、语法检查
- perl支持词法加亮
- php支持词法加亮、语法检查
- plain支持词法加亮
- prolog支持词法加亮
- properties支持词法加亮、语法检查
- python支持词法加亮、语法检查
- r支持词法加亮、语法检查
- ruby支持词法加亮、语法检查
- scala支持词法加亮、语法检查
- scheme支持词法加亮、基本语法检查
- sed支持词法加亮、语法检查
- smalltalk支持词法加亮、语法检查
- snobol支持词法加亮、语法检查
- sqlite支持词法加亮、语法检查
- swift支持词法加亮、语法检查
- tcl支持词法加亮、语法检查
- tex支持词法加亮
- texinfo支持词法加亮
- troff支持词法加亮
- xml支持词法加亮、语法检查
- yacc支持词法加亮

以上不少词法和语法文件来自<https://github.com/antlr/grammars-v4>，特此致谢。

#### 图片

##### 位图

现已支持读写PNG、JPEG、GIF、BMP等格式图片，提供基本绘图操作和一些滤镜，但易用性惨不忍睹。

##### 向量图

未来希望支持SVG格式。

#### 视频

现已支持播放FLV（VP6画面加MP3音频）、MPEG-4（H.264/AVC）的视频。

#### 音频

现已支持播放MP3、AIFF（未压缩PCM）、WAV（未压缩PCM）、MPEG-4（AAC）格式的音频。

#### 文档

现已通过Apache PDFbox支持查看PDF文档，即将支持OpenDocument和Microsoft office文档的查看，稍后可能支持修改。

#### 压缩和归档文件

现已借助Apache Common Compress库支持支持AR、ARJ、CPIO、DUMP、JAR、7Z、TAR、ZIP等归档格式，支持Brotli、BZip2、DEFLATE、GZip、LZ4、LZ77、LZMA、LZW、PACK200、Snappy、XZ、Z等压缩方式。同时通过junrar库支持解压RAR文件。

### 辅助工具

#### 文件系统查看器

目前提供一个文件系统查看器，有待做得更友好。

#### 浏览器

目前提供JavaFX基于Webkit的浏览器，支持简单历史。

#### 终端模拟器

目前提供基于jediterm的终端模拟器。

#### 版本控制

即将利用JGIT和SVNKit这两个纯Java库提供Git和Subversion这两个常用版本控制的功能

#### 准备提供的工具

- 日历
- 字符表
- 录音
- 屏幕录制
- 网络调试
- 加密
- 邮件客户端
- 字典
- 桌面搜索
- 计算器

傻瓜编辑器在GPL（版本3或按你的意愿更新）下发布。

