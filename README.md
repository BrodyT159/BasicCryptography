# BasicCryptography
Contains my personal solutions for the first set of the Cryptopals crypto challenges. This project was a practical exercise in understanding how ciphers work by both building them and breaking them.

Key concepts I implemented include:

Data Encoding: Wrote functions for Hex, Base64, and raw byte conversions.

Cipher Implementation: Built both fixed and repeating-key XOR ciphers.

Cryptanalysis: Implemented statistical attacks to break ciphers, including: * Using frequency analysis to solve single-byte XOR keys. * Using Hamming distance to find the key length of a repeating-key XOR. * Block transposition to break the repeating-key cipher.
