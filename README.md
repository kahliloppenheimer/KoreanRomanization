# Korean Romanization

This tool takes in some Korean -> English translations and adds romanization next to them!

To run, simply
```
mvn compile exec:java -Dexec.arguments="myInputFile.txt"
```

Example inputs:
```
Your brother looks nice.
당신의 형/오빠/남동생은(는) 잘 생겨 보입니다.

The problem risulted difficult.  그 문제는 어려운 것으로 판단되었습니다.
```

Outputs:
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
