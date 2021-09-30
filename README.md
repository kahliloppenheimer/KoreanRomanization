# Korean Romanization

This tool takes in some Korean -> English translations and adds romanization next to them!

## How to run
Execute
```
mvn compile exec:java -Dexec.arguments="./foo/my-input-file.txt"
```
And the output will be written to `./foo/my-input-file-romanizations.txt`.

For example, to translate the entire included example translation book, simply run:
```
mvn compile exec:java -Dexec.arguments="src/main/resources/whole-book.txt"
```
And the output will be written to:
```
src/main/resources/whole-book-romanization.txt
```

Example inputs:
```
Your brother looks nice.
당신의 형/오빠/남동생은(는) 잘 생겨 보입니다.

The problem risulted difficult.  그 문제는 어려운 것으로 판단되었습니다.
```

Example outputs:
```
Your brother looks nice.
당신의 형/오빠/남동생은(는) 잘 생겨 보입니다.
(dangsinui hyeong/oppa/namdongsaeng-eun(neun) jal saenggyeo boimnida.)

The problem risulted difficult.
그 문제는 어려운 것으로 판단되었습니다.
(geu munjeneun eoryeoun geoseuro pandandoe-eotseumnida.)

Or by appellative (essere chiamato, essere detto, essere soprannominato), elective (essere eletto, essere nominato, essere proclamato), estimative, essere ritenuto) or effective verbs.
또는 보통명사, 선출되거나, 평가할 수 있는 또는 사실상의 동사.
(ttoneun botongmyeongsa, seonchuldoegeona, pyeonggahal su inneun ttoneun sasilsang-ui dongsa.)
```

## Input requirements
Input must follow a rough format of:
```
${englishText}${whitespace}${koreanText}\n
${englishText}${whitespace}${koreanText}\n
```
A few special exceptions to formatting were encoded in the logic to handle the particular
formatting of the main test source text: `src/main/resources/whole-book.txt`.
