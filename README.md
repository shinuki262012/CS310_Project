# Algorithms for compressed text

## Decription

Efficient grammar-based compression algorithms and transformations between SLP and LZ77 representations.

---

## License

Copyright © 2022 Tianlong Zhong

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

---

## Compilation and usage

### Compilation

To compile: type the following in the directory containing this README.

> javac -d bin .\src\slp\util\*.java .\src\slp\text_slp\*.java .\src\slp\lz77_slp\gzip_parser\*.java .\src\slp\lz77_slp\*.java .\src\slp\Main.java

To run: type the following in `bin`

> java slp.Main

###Usage
**Main menu**
Run the program will display the main menu providing 3 options:

1. Compress text into SLP
2. Convert LZ77 to SLP
3. Convert SLP to LZ77

**Compress text into SLP**
On input '1', the user is prompted to choose the offline or online version of the compression algorithm.

**Offline**

1. text to cfg
   compress the input text file into a slp(cfg), result will be saved as a '.cfg' file.
2. _cfg to grammar tree_
   visualise the '.cfg' file as a parse tree, printed in terminal.
3. _cfg to text_
   decompress the '.cfg' into text, result will be saved as a new file with suffix '(1)'.

**Online**

1. text to succinct grammar
   compress the input text file into a compact grammar, result will be saved as a '.slp' file.
2. text to cfg
   compress the input text file into a slp(cfg), result will be saved as a '.cfg' file.
3. cfg to succinct grammar
   Convert the input '.cfg' file into '.slp' file.
4. cfg to grammar tree
   visualise the '.cfg' file as a parse tree, printed in terminal.
5. succinct grammar to text
   decompress the '.slp' file into text, result will be saved as a new file with suffix '(1)'.
6. cfg to text
   decompress the '.cfg' into text, result will be saved as a new file with suffix '(1)'.

**Convert LZ77 to SLP**
convert a GZIP compressed ('.gz') file into a SLP('.cfg') file.

**Convert SLP to LZ77**

1. slp to lz77
   convert the input '.cfg' file into a LZ77 factorization('.lz77')
2. decompress lz77
   decompress a LZ77 factorization('.lz77' file) into text, saved as a new file with suffix '(1)'.

### Test

Test data can be found in `test` directory.
