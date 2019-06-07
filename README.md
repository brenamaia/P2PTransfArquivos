# Rede P2P para compartilhamento de arquivos

Este repositório contém os códigos referentes à implementação de uma rede (P2P) para compartilhamento de arquivos. O usuário poderá baixar um arquivo (em formato pdf) que esteja disponível em várias máquinas conectadas na rede.

A arquitetura do sistema é composta por um servidor tracker, três peers servidores e um peer requerente. O peer requerente envia ao Tracker o nome do arquivo desejado, o Tracker verifica quais servidores possuem o arquivo e retorna o endereço IP e porta desses servidores. O peer requerente verifica a latência dos servidores e para aqueles que estão mais distantes, solicita um bloco maior do arquivo. O servidor divide o bloco de acordo com os tópicos do arquivo e envia para o peer requerente, que ao receber todos os blocos salva em pdf.
