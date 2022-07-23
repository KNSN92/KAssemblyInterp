# KAssemblyasennburiInterp
## 説明
my making a programing lang.
これは、私のJAVAの練習がてら作った物で、簡単なプログラムを作成できます。仕様は後付けでどんどんつけていき、不具合が見つかれば修正する、新しい機能をつけたいと思えば即興でつける、不具合が見つかれば...
と言う風に作ってきた物です。デバッグは適当にしているので、バグがあるかもしれませんが、ご容赦ください。^^;<br>
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
つまり、内部では要素はすべて数字として扱われます。しかし、後述する方法で新しくコマンドを作成するときにとあるメソッドをオーバーライドすることで、文字を扱うことは可能です。<br>
　<br>
プログラムは最初ですべて大文字に変換されるため、大文字小文字は好きに組み合わせてもらって構いません。<br>
ですが、その関係で、後述する方法で新しくコマンドを作成する場合、コマンド名は必ず大文字で実装する必要があります。<br>
<br>
　<br>
変数についてですが、今の所、プログラム内で変数を新たに作成する事は出来ません。<br>
しかし、後述する方法で新しくコマンドを作成して、プログラムを書けば、実装することは可能です。私も、近いうちに実装を予定しています。<br>
　<br>
変数には、"読み取り可能か"と"書き込み可能か"と言う2つのパラメーターがあり、ブーリアン値の配列によって管理されています。<br>
読み取り不可能に設定した変数を読み取ろうとする、または書き込み不可能に設定した変数に書き込みしようとした場合、エラーが出てプログラムが終了してしまいます。気を付けてください。<br>
　<br>
この言語にはgoto命令が含まれています。今の所、制御機構には、gotoを使うしかなく、スパゲッティコードとなる可能性が高いです。<br>
しかも、gotoの行数指定には、行数を数字で直接入力する必要があり、行を追加するなどした場合、gotoを一つ一つ書き換えないとエラーになる恐れがあります。<br>
これについては、いずれ、gotoを代替する新しい制御機構を導入予定です。<br>
　<br>
## 各デフォルトコマンドの仕様
　<br>
### 計算系
　<br>
結果はOP変数に格納されます。適宜別の変数に入れ替えるなどを必要とします。<br>
計算系のコマンドは、"MOD"(引数2つ)を除いて全て引数の長さを無限に伸ばすことが出来ます。<br>
　<br>
ADD:引数の全ての数を足します。<br>
SUB:最初の引数からそれ以降の引数を引きます。<br>
MLT:引数の全ての数を乗算します。<br>
DIV:最初の引数からそれ以降の引数を除算します。<br>
MOD:引数1と引数2の余りを求めます。<ber>
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
<br>
### 制御系
　<br>
プログラムを制御する際に用いり、高度な制御をする場合には必須になります。<br>
ですが、コードの可読性が著しく下がってしまう可能性があります。それは、自分自身にとっても、そのプログラムを読む人にとっても大きな障害となります。出来るだけ控えることを推奨します。<br>
　<br>
GOTO:指定された行数にジャンプします。指定された行数が、コードの範囲内に収まっている必要があります。<br>
EXIT:プログラムを終了します。引数はありません。<br>
<br>
### その他
　<br>
上記のいずれにも当てはまらないコマンドです。<br>
　<br>
DSP:引数で指定した内容を表示します。今の所、数字しか指定できないので、数字しか表示できません。引数は無限に伸ばすことが出来、その場合、引数と引数の間に半角スペースが入った状態で表示されます。<br>
<br>
 ## 新しいコマンド追加チュートリアル
 <br>
<br>
 ##今後の予定
 <br
 ##今後の予定
 <br>
