# Fantom Guardian
Decentralized, trustless, and anonymous crypto wills. A switch that triggers at death. User activity resets the countdown. 
Built on the Fantom Blockchain.

[![Playstore](https://i.imgur.com/egBW0oo.png)](https://play.google.com/store/apps/details?id=com.penguinstudios.fantomguardian)

https://www.youtube.com/watch?v=56hPIc7Fq-0

# Inspiration
The app was inspired by the need for a more secure and transparent way of managing digital assets after death. While some may opt for a lawyer to handle their will and distribute their crypto assets, there are inherent risks in entrusting private keys to a third party. In the event of a security breach, the private keys could be compromised, putting the entire estate at risk. Our solution is decentralized and trustless, ensuring that the wishes of the deceased are executed without any reliance on a single individual or institution.

# Built with

Fantom Guardian is developed natively on Android utilizing Java. The smart contract is written in Solidity. To ensure secure communications, messages are AES encrypted on-chain. For the generation of the decryption key, we employ the built-in mnemonic utilities of the web3j library.

The app uses:

- The Web3j Ethereum library
- MVVM Clean Architecture 
- RxJava
- Room
- Dagger Hilt

The contracts are deployed on the Fantom Opera Mainnet

https://ftmscan.com/address/0x9fb413c7cbf273f4e1d6efca17e6fc8dd13fa9be
