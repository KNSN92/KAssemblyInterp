# KAssemblyInterp
## 説明
my making a programing lang.
これは、私のJAVAの練習がてら作った物で、簡単なプログラムを作成できます。最初はアセンブリ言語を目指していましたが、途中から普通に新しい言語を作ろうと方針を変更しました。今でも、若干その名残が残っています。仕様は後付けでどんどんつけていき、不具合が見つかれば修正する、新しい機能をつけたいと思えば即興でつける、不具合が見つかれば...
と言う風に作ってきた物です。デバッグは適当にしているので、バグがあるかもしれませんが、ご容赦ください。^^;<br>
このプログラムは、"apache license 2"のもとで配布されています。
<br>
## 基本的な仕様
　<br>
まず基礎的なことです。<br>
　<br>
プログラムの書き方は、"コマンド 要素1 要素2 要素3 ..."<br>
と言う風に書いていきます。<br>
　<br>
要素は基本的に数字と変数だけです。<br>
例外的に、"True"は1として、"False","Null","Nil","None","Void"と言った物は0に内部で置換されます。<br>
つまり、内部では要素はすべて数字として扱われます。しかし、新しくコマンドを作成するときにgetInitResultメソッドをオーバーライドすることで、文字を扱うことは可能です。<br>
　<br>
プログラムは最初ですべて大文字に変換されるため、大文字小文字は好きに組み合わせてもらって構いません。<br>
ですが、その関係で、後述する方法で新しくコマンドを作成する場合、コマンド名は必ず大文字で実装する必要があります。<br>
　<br>
変数には、"読み取り可能か"と"書き込み可能か"と言う2つのパラメーターがあり、ブーリアン値の配列によって管理されています。<br>
読み取り不可能に設定した変数を読み取ろうとする、または書き込み不可能に設定した変数に書き込みしようとした場合、エラーが出てプログラムが終了してしまいます。気を付けてください。<br>
　<br>
この言語にはgoto命令が含まれています。今の所、制御機構には、gotoを使うしかなく、スパゲッティコードとなる可能性が高いです。<br>
~しかも、gotoの行数指定には、行数を数字で直接入力する必要があり、行を追加するなどした場合、gotoを一つ一つ書き換えないとエラーになる恐れがあります。~(ラベルを使うよう変更しました。)<br>
これについては、いずれ、gotoを代替する新しい制御機構を導入予定です。<br>
<br>
コメント文は、"#"を用います。#から行末までの文字は無視されます。ですが、複数の#があると、最初の#から最後の#までの文字が無視されます。
<br>
## 各デフォルトコマンドの仕様
　<br>
### 計算系
　<br>
結果はOP変数に格納されます。適宜別の変数に入れ替えるなどを必要とします。<br>
計算系のコマンドは、"MOD","ABS","POW"(ABSは引数1つ、他引数2つ)を除いて全て引数の長さを無限に伸ばすことが出来ます。<br>
　<br>
ADD:引数の全ての数を足します。<br>
SUB:最初の引数からそれ以降の引数を引きます。<br>
MLT:引数の全ての数を乗算します。<br>
DIV:最初の引数からそれ以降の引数を除算します。<br>
MOD:引数1と引数2の余りを求めます。<br>
ABS:引数の絶対値を返します。<br>
POW:引数1を引数2で累乗します。<br>
<br>
### 変数操作系
　<br>
主に変数を操作するのに使います。この系統のコマンドでは、少し特殊な記述が必要になります。<br>
これらのコマンドは、変数を指定する必要があるので、それ専用の引数を書く場所が決められています。<br>
ですが、そこは従来通り変数の名前を書くだけで自動で認識識別してくれます。<br>
また、その際、書き込み不可に設定された変数を書き換えようとした場合、エラーが出ます。<br>
　<br>
SET:引数1で指定された変数に引数2で指定した数を代入します。その際、元からあった内容は上書きされます。<br>
MOV:引数1で指定された変数の内容を引数2で指定した変数に移動させます。<br>
SWP:引数1で指定された変数の内容と引数2で指定された変数の内容を入れ替えます。<br>
VAR:新しく変数を作成します。最初にCONSTもしくはFINALを書くと定数になります。その次に、変数名を書き、最後に任意で最初から代入しておく数を書きます。書かなかった場合、0が代入されます。<br>
<br>
### 制御系
　<br>
プログラムを制御する際に用いり、高度な制御をする場合には必須になります。<br>
ですが、コードの可読性が著しく下がってしまう可能性があります。それは、自分自身にとっても、そのプログラムを読む人にとっても大きな障害となります。出来るだけ控えることを推奨します。<br>
　<br>
GOTO:指定されたラベル(その他の項目のラベルコマンドを参照。)にジャンプします。(最終的には削除します)<br>
EQRL:引数2と引数3が同じなら、引数1の指定されたラベル(その他の項目のラベルコマンドを参照。)へGOTOする。(最終的には削除します)<br>
EQRLNOT:EQRLの条件を反転した物です。つまり、EQRLコマンドは"=="演算子で比較していましたが、EQRLNOTコマンドでは、"!="演算子を用いているような物です。
EXIT:プログラムを終了します。引数はありません。<br>
<br>
### その他
　<br>
上記のいずれにも当てはまらないコマンドです。<br>
　<br>
DSP:引数で指定した内容を表示します。今の所、数字しか指定できないので、数字しか表示できません。引数は無限に伸ばすことが出来、その場合、引数と引数の間に半角スペースが入った状態で表示されます。<br>
EXPORT:引数の内容を配列に追加。そして、Engine.getExportValues()でその内容を取り出すことが出来ます。配列は次のrunメソッドが実行されるまで保存されます。これによって、Java間で情報のやり取りがより簡単にできるようになります。<br>
LABEL:新しくラベルを作ります。ラベルは、GOTO等の行数指定方法を代替する物で、ラベルが定義された行が、GOTOなどで飛ぶ先となります。~今の所、ラベルを定義する前の行で、そのラベルを指定することは出来ませんが、近々実装予定なので、しばらくお待ちください。~(対応しました。)<br>
 <br>
 ## 新しいコマンド追加チュートリアル
<br>
 工事中！！
 <br>
 
 ## 今後の予定
 
<br>
 今後の予定についてですが、まず、GOTOに頼らないより高度な制御構文を導入する予定です。具体的には、IF,FOR,WHILE,SWITCH等と言ったより安全で現代の言語に近いような構文も追加するつもりです。<br>
 
 更に、関数機能を追加することも考えています。<br>
 今の案としては、"Function function名 入力引数1　入力引数2 ..."と言う風に実装しようと考えています。ですが、関数機能を実装するには課題が多く、まず第1に、スコープの実装が必要です。<br>
 今の所、変数一つ一つに付与できる設定でアクセス出来る範囲を制限することで、実装することを目指しています。<br>
 <br>
 また、かなり将来的な話になりますが、別のファイル間でコードをインポートしたり、ライブラリとしてインポートできるような機能、文字列型の実装、配列型の実装などがあります。<br>
 ですが、これらの機能が実装されるには時間がかかる可能性が高いです。まず、やる気になったら始めること。javaのスキルが足りないこと。マイペースでゆっくり開発していくので、かなり先の話になる可能性が高いです。<br>
