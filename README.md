Some code to find Java hashCode() collisions.

`cz.topolik.hashcodecollisions.StringHashCodeCollisionsMutator` to find collisions by mutating some word - mangles the word but keeps length

**Example:**
```
mvn package

java -cp target/*.jar cz.topolik.hashcodecollisions.StringHashCodeCollisionsMutator machinator 4

Searching collisions for: 'machinator' (hashCode: -1808240076) into depth: 4
Total combinations to be tried: 2374281.0
Found 0 collisions for position 2
Found 0 collisions for position 1
Found 0 collisions for position 0
Found 0 collisions for position 3
Found 0 collisions for position 5
Found 1 collisions for position 6 (at): c6
Found 1 collisions for position 4 (in): k0
Found 1 collisions for position 7 (to): v1
Found 1 collisions for position 8 (or): q4
Found 0 collisions for position 2
Found 1 collisions for position 5 (nat): nc6
Found 1 collisions for position 4 (ina): k0a
Found 2 collisions for position 3 (hin): hk0 j-0
Found 0 collisions for position 0
Found 0 collisions for position 1
Found 3 collisions for position 6 (ato): av1 c81 c6o
Found 3 collisions for position 7 (tor): v34 tq4 v1r
Found 7 collisions for position 6 (ator): av34 c6or av1r atq4 c6q4 c834 c81r
Found 2 collisions for position 2 (chin): chk0 cj-0
Found 2 collisions for position 3 (hina): hk0a j-0a
Found 3 collisions for position 4 (inat): inc6 k0at k0c6
Found 3 collisions for position 5 (nato): nc81 nc6o nav1
Found 0 collisions for position 0
Found 0 collisions for position 1
Collisions: [machinator, machinatq4, machinav1r, machinav34, machinc6or, machinc6q4, machinc81r, machinc834, machk0ator, machk0atq4, machk0av1r, machk0av34, machk0c6or, machk0c6q4, machk0c81r, machk0c834, macj-0ator, macj-0atq4, macj-0av1r, macj-0av34, macj-0c6or, macj-0c6q4, macj-0c81r, macj-0c834]
Found 24 collisions in 1291 ms
```

`cz.topolik.hashcodecollisions.StringHashCodeCollisionsPostfixator` to find collisions to original word by creating a postfix of chosen word

**Example:**
```
mvn package

java -cp target/*.jar cz.topolik.hashcodecollisions.StringHashCodeCollisionsPostfixator http://valid-server.com/redirectPage http://evil.com/myPage 8

Searching collisions for target: 'http://evil.com/myPage' (hashCode: -450747663) with source: http://valid-server.com/redirectPage (hashcode: -491303423) into depth: 8
Not found in depth 2
Not found in depth 3
Not found in depth 1
Not found in depth 4
Not found in depth 5
Found in depth 6: http://evil.com/myPageG^MNG_ hashCode(): -491303423
Found in depth 7: http://evil.com/myPage@LD\SUX hashCode(): -491303423
Found in depth 8: http://evil.com/myPage@DQTCBNX hashCode(): -491303423
Time: 7310
```# java-hashcode-collisions
