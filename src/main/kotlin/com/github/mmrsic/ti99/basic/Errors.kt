package com.github.mmrsic.ti99.basic

sealed class TiBasicException(msg: String) : Exception(msg)

class CantDoThatException : TiBasicException("CAN'T DO THAT")
class BadLineNumber : TiBasicException("BAD LINE NUMBER")
