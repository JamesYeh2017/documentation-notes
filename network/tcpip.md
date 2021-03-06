# TCP/IP
- 2018/05/17
- 一個 `非 MIS` 的觀點寫的筆記


# 1. 網段位址 (Network ID) + 主機位址 (Host ID)
### 1-1. Network ID
- 具備唯一性
- bits部分 不可全為 0, 其實沒有這東西, 但 `0.0.0.0` -> 預設路由的概念(別理他)
- bits部分 不可全為 1, 其實沒有這東西, 但 `255.255.255.255` -> `Limited Broadcast`(也可別理他)
- 不可為 `127` 開頭

#### 如何計算 `網段位址`
1. `IP Address` 轉成 2進制
2. `Subnet Mask` 轉成 2進制
3. 以上2者作 **AND** 運算

```
IP = 138.239.149.238/255.255.224.0  (( 224 -> 二進位 = 11100000 ))
即為 138.239.149.238/19

149 -> 10010101
224 -> 11100000
AND    --------
       10000000 -> 128

所以 Network Number 為 255.255.128.0
```

### 1-2. Host ID
- 具備唯一性
- bits部分 不可全為 0 -> `Network Number` 在用
- bits部分 不可全為 1 -> `Subnet Broadcast` 在用


<font color="red">有點重要</font>: 如何知道 `Network ID` 及 `Host ID` 個佔用了哪些 bits? -> 依照 `IP Class` 及 `Subnet Mask` 判斷!


# 2. IP Class (用 FF 代表 255)
Class             |   A                 |   B       |   C       |   D       |   E   
----------------- | ------------------- | --------- | --------- | --------- | ---------
Starts at         | 0~                  | 10~       | 110~      | 1110~     | 1111
Len(Network ID)   | 8 bits              | 16 bits   | 24 bits   | X         | X
Len(Host ID)      | 24 bits             | 16 bits   | 8 bits    | x         | X
IP Range          | 1.0.0.0 ~ 126.FF.FF.FF | 128.0.0.0 ~ 191.FF.FF.FF | 192.0.0.0 ~ 223.FF.FF.FF | 224.0.0.0 ~ 239.FF.FF.FF| 240.0.0.0 ~ FF.FF.FF.FF
Network IP range  | 1.0.0.0 ~ 126.0.0.0 | 128.0.0.0 ~ 191.FF.0.0 | 192.0.0.0 ~ 223.FF.FF.0 | X | X
Host IP range     | 0.0.1 ~ FF.FF.254   | 0.1 ~ FF.254 | 1 ~ 254 | X | X
count(Network)    | 2^(8-1) - 2         | 2^(16-2)  | 2^(24-3) | X | X
count(Host)       | 2^24 - 2            | 2^16 - 2  | 2^8 - 2  | X | X

### 在還沒切割 Subnet之前, 辨識 IP 的 範例 (對於非網管, 越下面越偏)
```
127.0.0.1          ->    Loopback
100.256.222.222    ->    X
223.255.12.2       ->    Class C
129.255.255.254    ->    Class B
10.138.2.88        ->    Class A
192.168.0.133      ->    Class C 的 Private IP
172.16.255.98      ->    Class B 的 Private IP
10.131.8.231       ->    Class A 的 Private IP
127.132.10.138     ->    Loopback
0.138.4.100        ->    Network 全為 0
192.72.254.255     ->    Host 全為 1
255.255.255.255    ->    Broadcast
224.0.0.1          ->    Class D
242.98.97.96       ->    Class E
```

#### 備註 : 私有 IP位址
Class | Definition     | Range
----- | -------------- | --------
A     | 10.0.0.0/8     | 10.0.0.1 ~ 10.255.255.254
B     | 172.16.0.0/12  | 172.16.0.1 ~ 172.31.255.254
C     | 192.168.0.0/16 | 192.168.0.1 ~ 192.168.255.254


# 3. 子網路切割 && 延伸

### 3-1. 子網路切割 - 固定長度子網路遮罩(Fixed Length Subnet Mask, FLSM) 

> 公式 : `2^n >= 要分割的子網段數量`

範例 3-1-1:
```
----- Question -----
把 131.210.0.0 劃分成 6個子網路, 求
A. 子網路遮罩
B. 各個子網路可分配的主機數量
C. 各個子網路的範圍

----- Answer -----
class B 網段, Default Mask = 255.255.0.0
11111111 11111111 00000000 00000000

切個成6個子網路, 那就得借用 2^n >= 6, n >= 3 

Network ID 要跟 Host ID 借用 3個 bits!!
11111111 11111111 11100000 00000000
                  ↑↑↑

即為 11100000 => 224
所以, 切割後的 子網路遮罩 為 255.255.224.0
11111111 11111111 11100000 00000000

剩餘的 5+8個位元, 可用來分配給 Host ID, 則可配置 2^13 - 2 = 8190台
```

### 3-2. 子網路切割 - 可變長度子網路遮罩(Variable Length Subnet Mask, VLSM)

- 簡易概念: 反覆使用 FLSM 的手法來切割

#### 範例 3.2.1 - 說明 FLSM 浪費 ip
```
你有一個 Class C 的網段, 老闆說他要幾個子網域(要作區域網路), 你幫忙切個 ip
網域x 有 60 台電腦
網域y 有 28 台電腦
網域z 有  6 台電腦
Network Number 為 192.168.0.0

如果依照 FLSM 的切法, 2^n >= 3, 則 n 取 2
Network ID 跟 Host ID 借用 2 個 bits

11000000 10101000 00000000 00|xxxxxx (這次要用)
11000000 10101000 00000000 01|xxxxxx (這次要用)
11000000 10101000 00000000 10|xxxxxx (這次要用)
11000000 10101000 00000000 11|xxxxxx (因為只切3段, 這邊算是可以保留給將來使用)
     Origin Network ID     ↑↑| Host ID

切割後, Host IP 剩下 6個位元, 2^6 - 2 = 62 個
分配下去之後發現超哭邀,
網域x 分配 62個 ip -> 浪費  2 個 ip
網域y 分配 62個 ip -> 浪費 34 個 ip
網域z 分配 62個 ip -> 浪費 56 個 ip

一大堆浪費掉了!!  另外, 如果老闆說, 他還要一個 Subnet 放 65台電腦, 你不就 GG了?
```

#### 範例 3.2.2 - 改用 VLSM 減少浪費
```
同上範例
網域x 有 60 台電腦
網域y 有 28 台電腦
網域z 有  5 台電腦
Network Number 為 192.168.0.0/24
11000000 10101000 00000000 00000000
                           ↑↑↑↑↑↑↑↑
                          取最後一個位元組, 由左而右, 逐一切割

借用 1個 bit後,
192.168.0.0|0000000~1111111 -> 192.168.0.0/25
192.168.0.1|0000000~1111111 -> 192.168.0.128/25
(一個 Network ID 有 127-2 台)

借用 2個 bit
192.168.0.00|000000~111111 -> 192.168.0.0/26
192.168.0.01|000000~111111 -> 192.168.0.64/26
192.168.0.10|000000~111111 -> 192.168.0.128/26
192.168.0.11|000000~111111 -> 192.168.0.192/26
(一個 Network ID 有 63-2 台)

第一組分配給 網域x
192.168.0.0/26 分配給 60 台電腦的 Subnet, 可用 ip 區間為
192.168.0.[1~61]  (浪費 1 個 ip)

之後, 再把剩下的 192.168.0.01|000000 繼續借用 bit

借用 3個 bit
192.168.0.010|00000 -> 192.168.0.64/27
192.168.0.011|00000 -> 192.168.0.96/27
(一個 Network ID 有 31-2 台)
第一組分配給 網域y
192.168.0.64/27 分配給 28 台電腦的 Subnet, 可用 ip 區間為
192.168.0.[65~95] (只浪費 1 個 ip)

之後, 再把剩下的 192.168.0.011|00000 繼續借用 bit

借用 4個 bit
192.168.0.0110|0000 -> 192.168.0.96/28
192.168.0.0111|0000 -> 192.168.0.112/28 (這後面的東西, 這次用不到了~~ 保留下來將來用)
(一個 Network ID 有 15-2 台)

借用 5個 bit
192.168.0.01100|000 -> 192.168.0.96/28
192.168.0.01101|000 -> 192.168.0.104/28
(一個 Network ID 有 7-2 台)
第一組分配給 網域z
192.168.0.96/28 分配給 5 台電腦的 Subnet, 可用 ip 區間為
192.168.0.[97~103] (沒有浪費 ip)

------------- 結果 -------------
網域x 有 60 台電腦 192.168.0.[1~62]  , 浪費 1 個 ip
網域y 有 28 台電腦 192.168.0.[65~95] , 浪費 1 個 ip
網域z 有  6 台電腦 192.168.0.[97~103], 浪費 0 個 ip
```



### 3-3. 子網路延伸 - Classless Inter-Domain Routing (CIDR), 也被稱為 Supernetting

> CIDR核心精神: 完全不理會當初對 Class 的定義, IP 網路的劃分全部都採用 `網路遮罩` 來決定 這些 IP Address 是否屬於 同一個網段上的主機.

> 故事情境: 老闆說, 給我弄出個 800台電腦的子網路~~~.  恩...  Class B 的網路, Host ID 可以有 好幾萬台, 但 Class C 的網路, 只有 255 台!! 這樣的話, 把 4個 255 台的 Class C 合併起來不就好了!!!! (運用 CIDR 的精隨)

#### Supernetting(速算概念)
- 合併後的 `Network Number`, 就是`原 Network` 值最小的那個
- 合併後的 `Subnet Mask`, 就是 `「原 Subnet Mask 的個數」 - n` , 其中 2^n >= Network 數量
- 要做 subnetting 的子網域們, Network Number 必須要連續, 且可被 2 整除
- 以「10進制」表示, 最小的 `Network Number` 最右方不為 0 的值, 是否可被 Network們 的數量整除, 若是, 則可 supernetting; 否則無法作 subnetting


#### 範例 3-3-1 (老實的計算):
```
# 有4個子網段, 打算把它合併起來, 來滿足 「能把 800 台電腦串在一起(同網段), 卻又不造成大量浪費(用 Class B啦)的作法」, 網路編號(Network Number)如下:
192.168.4.0/24
192.168.5.0/24
192.168.6.0/24
192.168.7.0/24

# 轉成 2進位 逐bit 對齊
11000000 10101000 00000100 00000000
11000000 10101000 00000101 00000000
11000000 10101000 00000110 00000000
11000000 10101000 00000111 00000000
________ ________ ______xx    (因為 2^n >= 4, n >= 2, 跟左邊借用 2 bits)

# 逐 bit 觀察, 是不是所有的值都相同, 以上相同的標 _ , 不同的標 x, 
假如「xx」的部分, 各種排列組合都有 => 可以做 supernetting
假如「xx」的部分, "並非"各種排列組合都有, 要看缺乏的排列組合的那些網段, 是不是「未分配」, 若是 => 可以做 supernetting
所以, 此範例可以做 Supernetting!!  來開始 Super 了~~

把「_」部分, 當成「合併後的 Network ID」
把「x」部分, 當成「合併後的 Host ID」
11000000 10101000 000001|00 00000000
11000000 10101000 000001|01 00000000
11000000 10101000 000001|10 00000000
11000000 10101000 000001|11 00000000
 NEW Network ID         | NEW Host ID

# 合併後的 Subnet Mask 為 255.255.252.0/22
11111111 11111111 11111100 00000000

# 合併後的 Network Number 為 192.168.4.0
11000000 10101000 00000100 00000000

可用 subnet ip 為
192.168.000001|00 00000000~11111111 -> 192.168.4.[1~255] (Host ID 不可全為 0)
192.168.000001|01 00000000~11111111 -> 192.168.5.[0~255]
192.168.000001|10 00000000~11111111 -> 192.168.6.[0~255]
192.168.000001|11 00000000~11111111 -> 192.168.7.[0~254] (Host ID 不可全為 1)
```


#### 範例 3-3-2 (老實的計算):
```
200.12.12. 64/28
200.12.12. 80/28
200.12.12. 96/28
200.12.12.112/28

11001000 00001100 00001100 01000000/28
11001000 00001100 00001100 01010000/28
11001000 00001100 00001100 01100000/28
11001000 00001100 00001100 01110000/28
________ ________ ________ __xxxxxx

# 當然, 上述的其餘網段「x」部分 如果沒被分配的話, 才適合做 Supernetting

Net Network ID: 200.12.12.64/26
11001000 00001100 00001100 01000000

New Subnet Mask: 255.255.255.192/26
11111111 11111111 11111111 11000000
```


#### 範例 3-3-3 (速算法):
```
# 1. 可supernetting (6/2組subnet個數整除, Network ID 連續)
192.168.6.0/24
192.168.7.0/24

# 2. 不可supernetting (6/4組subnet個數的餘數 != 0, Network ID 連續)
192.168.6.0/24
192.168.7.0/24
192.168.8.0/24
192.168.9.0/24
```


# 4. Broadcast 廣播

```
  router                D    E    F
    |                   |    |    |    
----●----O----O----O----O----O----O----●----
         |    |    |                   |
         A    B    C                 router

A: 192.168.1.1
B: 192.168.1.2
C: 192.168.1.3
D: 192.168.2.1
E: 192.168.2.2
F: 192.168.2.3
```
- 沒有被 `router` 隔開的 Network, "建議"只規畫一個 `IP Network`
- 被 `router`隔開的 Network, 必須有不同的 `IP Network`
- 若 `192.168.1.1` 送出 ip封包 -> `255.255.255.255`, 則 **B~F** 都會收 `Limited broadcast`
- 若 `192.168.1.1` 送出 ip封包 -> `192.168.2.255`  , 則 **D~F** 都會收 `Subnet broadcast`

# 網卡

1. 我家 CentOS7 顯示的所有網卡

> On Linux, 顯示網卡資訊, 語法: `ip addr show` / `ip addr` / `ip a` , 此指令幾乎等於 `ifconfig`; 若只顯示 IPv4, 則使用 `ip -4 addr`

```sh
$ ip -4 addr
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN qlen 1
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
2: enp1s0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP qlen 1000
    inet 192.168.124.73/24 brd 192.168.124.255 scope global dynamic enp1s0
       valid_lft 859057sec preferred_lft 859057sec
4: virbr0: <NO-CARRIER,BROADCAST,MULTICAST,UP> mtu 1500 qdisc noqueue state DOWN qlen 1000
    inet 192.168.122.1/24 brd 192.168.122.255 scope global virbr0
       valid_lft forever preferred_lft forever
6: docker_gwbridge: <NO-CARRIER,BROADCAST,MULTICAST,UP> mtu 1500 qdisc noqueue state DOWN
    inet 172.18.0.1/16 brd 172.18.255.255 scope global docker_gwbridge
       valid_lft forever preferred_lft forever
7: docker0: <NO-CARRIER,BROADCAST,MULTICAST,UP> mtu 1500 qdisc noqueue state DOWN
    inet 172.17.0.1/16 brd 172.17.255.255 scope global docker0
       valid_lft forever preferred_lft forever

# 6 為 Docker bridge network, 在特定情況下, 自行建立的.
```


# ping 不到 win10



> VM裏頭, 都ping不到Windows10, 該如何設定? <br />
  開始 > Windows系統管理工具 > 具有進階安全性的 Windows Defender 防火牆 / 輸入規則 > `檔案及印表機共用(回應要求 - ICMPv4-In) 網域` <- 啟用規則, Subnet底下就 ping得到了!!



# 零星片段 && 名詞
- 應用程式協定的標準(Application Protocol Standards) `Socket API`
- 非連結式封包傳遞服務(Connectionless Packet Delivery Service), ex: IP協定、UDP協定
- 端對端確認(End-to-End Acknowledgements), `TCP (three-way handshake)` (建立 Session)
- Network: 網路 網段
- Network Segment 網段 網路區段
- Automatic Private IP Addressing (APIPA) : 自動私人IP定址