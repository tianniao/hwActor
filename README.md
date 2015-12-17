Actor模型。它的主要思想就是用一些并发的实体，称为actor，他们之间的通过发送消息来同步。
Actor之间只有发送消息这一种通信方式，大量的消息可以同时执行, 消息让Actor之间解耦，消息发出之后执行成功还是失败，须要耗费多少时间，只要没有消息传递回来，这一切都和发送方无关。Actor模型的消息传递形式简化了并行程序的开发，使开发人员无需在共享内存环境中与“锁”、“互斥体”等常用基础元素打交道。

示例程序：
圆周率计算公式 π/4=1/1-1/3+1/5-1/7+1/9-1/11+1/13-1/15……1/（2n-1）-1/（2n+1)
main.run.PiQuarterActor根据传入message参数中的数字，计算该段区间的值。
如：1-1000 就是计算1/（2n-1）-1/（2n+1) n从1到1000的值
如：1000-2000 就是计算1/（2n-1）-1/（2n+1) n从1000到2000的值

main.run.PiSumActor根据传入message参数中的数字，累加起来，得到π/4

main.run.PrintActor根据message参数，打印该值。

main.run.MainRunOnce在runOnce()方法中打算计算圆周率公式中n从1到200000000，分成100个区间就是100个message，然后发送给PiQuarterActor，再到PiSumActor，最后PrintActor输出结果。


run java class main.run.MainRunMore in project to show the execute time of compute the PI.
(4 core Intel(R) Core(TM) i5-4590 CPU @ 3.30GHz)

Actor run is use actor to compute the PI
Single thread to compute the PI.

------0------
Actor start run
PI=3.141592651089420
Execute time(miliseconds):6776
------------------
Single thread start run
PI=3.141592651089420
Execute time(miliseconds):22984
------0------

------1------
Actor start run
PI=3.141592651089420
Execute time(miliseconds):6678
------------------
Single thread start run
PI=3.141592651089420
Execute time(miliseconds):22961
------1------

------2------
Actor start run
PI=3.141592651089420
Execute time(miliseconds):6671
------------------
Single thread start run
PI=3.141592651089420
Execute time(miliseconds):22224
------2------

------3------
Actor start run
PI=3.141592651089420
Execute time(miliseconds):6671
------------------
Single thread start run
PI=3.141592651089420
Execute time(miliseconds):22286
------3------

------4------
Actor start run
PI=3.141592651089420
Execute time(miliseconds):6699
------------------
Single thread start run
PI=3.141592651089420
Execute time(miliseconds):22193
------4------

