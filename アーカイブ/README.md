# 第1回 課題 採点スクリプト一式

ネットワーク応用 ／ 第1回「ソケットとポート」／ 課題 `NetProbe.java`

---

## 同梱物

| ファイル | 配布先 | 内容 |
|---|---|---|
| `check.ps1` | **学生** | 自己採点スクリプト（Windows / PowerShell） |
| `check.bat` | **学生** | `check.ps1` をダブルクリックで実行するためのラッパ |
| `check.sh` | **学生** | 自己採点スクリプト（macOS / Linux） |
| `EchoServer.jar` | **学生** | 演習・採点で使うエコーサーバ（※ 下記の手順で教員が作成） |
| `教員用_EchoServer.java` | 教員のみ | 上記 jar のソース。**学生に配布しない** |
| `教員用_NetProbe_reference.java` | 教員のみ | 模範解答。**学生に配布しない** |

> **配布しないファイルが2つあります。**
> `EchoServer.java` は第3回（`ServerSocket`）の答えそのものです。
> `NetProbe_reference.java` は第1回の課題の答えです。
> eclass には `check.ps1` / `check.bat` / `check.sh` / `EchoServer.jar` の4つだけを掲示してください。

---

## 授業前の準備（教員）

### 1. `EchoServer.jar` を作る

```
javac -encoding UTF-8 教員用_EchoServer.java
```

ファイル名を `EchoServer.java` に戻してからコンパイルしてください（クラス名と一致させる必要があります）。

```
rename 教員用_EchoServer.java EchoServer.java
javac -encoding UTF-8 EchoServer.java
jar --create --file EchoServer.jar --main-class EchoServer EchoServer.class
```

### 2. 採点スクリプトが通ることを確認する

模範解答を `NetProbe.java` という名前にして、同じフォルダで実行します。

```
rename 教員用_NetProbe_reference.java NetProbe.java

:: 別ウィンドウで
java -jar EchoServer.jar 40001

:: 元のウィンドウで
powershell -ExecutionPolicy Bypass -File check.ps1
```

`4 / 4 passed` になれば準備完了です。

### 3. ★ ケース4 を教室のネットワークで確認する

**この確認を飛ばさないでください。**

ケース4（`10.255.255.1` への接続）が `TIMEOUT` になるかどうかは、ネットワーク構成に依存します。学内ネットワークによっては、拒否や到達不能がすぐ返り、別の結果になります。

教室で実行して `TIMEOUT` にならない場合は、次のどちらかにしてください。

- 宛先を、教室から到達できない別のアドレスに差し替える（スクリプト冒頭の `DeadHost` / `DEAD_HOST`）
- ケース4 を採点対象から外す

いずれにせよ、**学生に「絶対 TIMEOUT になる」と言い切らないこと。**

---

## 学生への案内

1. 別のウィンドウで EchoServer を起動し、**そのまま放置する**

   ```
   java -jar EchoServer.jar 40001
   ```

2. `NetProbe.java` と同じフォルダで採点スクリプトを実行する

   - Windows：`check.bat` をダブルクリック（または `powershell -ExecutionPolicy Bypass -File check.ps1`）
   - macOS / Linux：`bash check.sh`

3. **`4 / 4 passed` になったら提出**

### 表示の読み方

```
PASS  ケース1  localhost:40001            -> CONNECTED
FAIL  ケース2  localhost:40099            -> 誰もいないポート
        - 出力が 6 行（5行ちょうどにする）
        - RESULT=OK（期待 REFUSED）
```

`FAIL` の下に、**何が仕様と違うかが1行ずつ出ます。** そこを直してください。

---

## 採点される4ケース

| # | 入力 | 期待される `RESULT` | 追加の確認 |
|---|---|---|---|
| 1 | `localhost 40001` | `CONNECTED` | — |
| 2 | `localhost 40099` | `REFUSED` | — |
| 3 | `no-such-host.invalid 40001` | `UNKNOWN_HOST` | `ADDR=-` であること |
| 4 | `10.255.255.1 40001` | `TIMEOUT` | 環境依存 |

### 各ケースで見ている項目

- 出力が**ちょうど5行**であること（デバッグ出力の消し忘れを検出）
- `RESULT` が期待値であること
- `RESULT` が **4種類（`CONNECTED` / `UNKNOWN_HOST` / `REFUSED` / `TIMEOUT`）以外**になっていないこと
- `HOST` と `PORT` が**引数そのまま**であること
- **スタックトレースが出ていない**こと
- ケース3 で `ADDR=-` になっていること

`ELAPSED_MS` の値は見ません。

---

## ポート番号を変えたいとき

スクリプト冒頭の変数を書き換えてください。既定は `40001`（EchoServer）と `40099`（誰もいないポート）です。

**PowerShell**

```powershell
powershell -ExecutionPolicy Bypass -File check.ps1 -OpenPort 41001 -ClosedPort 41099
```

**bash**

```bash
OPEN_PORT=41001 CLOSED_PORT=41099 bash check.sh
```

> この授業では **1024〜49151 の範囲**を使ってください。1024 未満は待ち受けに管理者権限が必要で、49152 以上は OS の動的割り当てとぶつかります。

---

## よくある問い合わせ

| 症状 | 原因と対処 |
|---|---|
| `localhost:40001 で EchoServer が待っていません` | EchoServer を起動していないか、同じウィンドウで起動して固まっている。**別ウィンドウで起動する** |
| `Address already in use` | 前に起動した EchoServer がまだ生きている。そのウィンドウで `Ctrl` + `C` |
| `COMPILE FAILED` | まずコンパイルエラーを直す。採点以前の段階 |
| 出力が 6 行になる | デバッグ用の `System.out.println` の消し忘れ |
| すべて `UNKNOWN_HOST` | `catch` の順番。広い例外を先に書くと、後ろの `catch` に到達しない |
| ケース4 だけ落ちる | 環境依存。実際の結果と環境を `prompt_log.md` の末尾に書けば減点しない |

---

## 動作要件

- JDK 17 以降（`javac` と `java` にパスが通っていること）
- Windows：PowerShell 5.1 以降
- macOS / Linux：bash 4 以降（`/dev/tcp` を使用。`sh check.sh` ではなく **`bash check.sh`** で実行）
