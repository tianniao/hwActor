Actor模型。它的主要思想就是用一些并发的实体，称为actor，他们之间的通过发送消息来同步。
Actor之间只有发送消息这一种通信方式，大量的消息可以同时执行, 消息让Actor之间解耦，消息发出之后执行成功还是失败，须要耗费多少时间，只要没有消息传递回来，这一切都和发送方无关。Actor模型的消息传递形式简化了并行程序的开发，使开发人员无需在共享内存环境中与“锁”、“互斥体”等常用基础元素打交道。