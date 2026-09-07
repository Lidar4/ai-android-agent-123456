package com.example.aiandroidagent.voice

import android.content.Context
import android.content.Intent
import android.speech.*
import android.speech.tts.TextToSpeech
import java.util.Locale

class SpeechRecognizerManager(private val context: Context, private val onText:(String)->Unit, private val onStatus:(String)->Unit) { private var recognizer:SpeechRecognizer?=null; var locale=Locale("bn","BD")
 fun start(){ if(!SpeechRecognizer.isRecognitionAvailable(context)){onStatus("Speech recognition unavailable");return}; recognizer=SpeechRecognizer.createSpeechRecognizer(context).apply{setRecognitionListener(object:RecognitionListener{override fun onResults(r:Bundle){r.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.let(onText);onStatus("Ready")};override fun onError(e:Int){onStatus("Voice error $e")};override fun onReadyForSpeech(p:Bundle?){onStatus("Listening")};override fun onBeginningOfSpeech(){};override fun onRmsChanged(v:Float){};override fun onBufferReceived(b:ByteArray?){};override fun onEndOfSpeech(){};override fun onPartialResults(r:Bundle?){};override fun onEvent(t:Int,p:Bundle?){} });startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).putExtra(RecognizerIntent.EXTRA_LANGUAGE,locale.toLanguageTag()).putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true))} }
 fun stop(){recognizer?.stopListening();recognizer?.destroy();recognizer=null}
}
class TTSManager(context:Context):TextToSpeech.OnInitListener { private val tts=TextToSpeech(context,this); override fun onInit(status:Int){if(status==TextToSpeech.SUCCESS)tts.language=Locale("bn","BD")}; fun speak(text:String){tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"agent-response")}; fun stop(){tts.stop()}; fun release(){tts.shutdown()} }
