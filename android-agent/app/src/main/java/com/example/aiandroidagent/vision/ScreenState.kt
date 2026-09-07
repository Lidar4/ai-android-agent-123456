package com.example.aiandroidagent.vision

data class ScreenState(val width:Int=0,val height:Int=0,val changed:Boolean=false,val image:ByteArray?=null)
class FrameProcessor { private var previousHash:Int?=null; fun process(bytes:ByteArray,width:Int,height:Int):ScreenState { val hash=bytes.contentHashCode(); val changed=previousHash!=hash; previousHash=hash; return ScreenState(width,height,changed,bytes) } }
interface VisionAnalyzer { suspend fun describe(image:ByteArray):Result<String> }
