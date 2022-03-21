package com.example.basic_ch08_webbrowser

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.basic_ch08_webbrowser.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityMainBinding    // 뷰바인딩

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        initViews()    // 뷰에 대해 선언
        bindViews()    //

    }

    // 뒤로 가기 실행 시 동작 오버라이딩
    override fun onBackPressed() {

        if (mainBinding.webView.canGoBack()) {  // 뒤로 갈 수 있는 경우에 뒤 히스토리로 이동동
           mainBinding.webView.goBack()
        } else {
            super.onBackPressed()   // 뒤로가기 누를시 앱이 종료
        }
    }

    // 웹뷰에 대해 연결
    private fun initViews() {
        mainBinding.webView.apply {
            webViewClient = WebViewClient()     // 웹뷰 클라이언트 객체 생성
            webChromeClient = WebChromeClient()    // 웹 크롬 뷰 클라이언트 객체 생성
            settings.javaScriptEnabled = true
            loadUrl(HOMEURL)
        }
    }

    private fun bindViews() {
        // EditText에서 액션 처리 view : 텍스트가 입력 되는 곳 , i : 발생하는 액션 구분 ,
        mainBinding.addressBar.setOnEditorActionListener { view, i, keyEvent ->

            if (i == EditorInfo.IME_ACTION_DONE) {
                val loadUrl = view.text.toString()
                // http나 https가 붙는지 확인해주는 함수 둘 중 아무것도 없을 경우 false 반환
                if (URLUtil.isNetworkUrl(loadUrl)) {
                    mainBinding.webView.loadUrl(loadUrl)
                } else {
                    mainBinding.webView.loadUrl("http://$loadUrl")
                }

            }
            return@setOnEditorActionListener false

        }

        mainBinding.backBtn.setOnClickListener {
            mainBinding.webView.goBack()    // 이전 히스토리로 넘어간다
        }

        mainBinding.forwardBtn.setOnClickListener {
            mainBinding.webView.goForward()
        }

        mainBinding.homeBtn.setOnClickListener {
            mainBinding.webView.loadUrl(HOMEURL)
        }

        // refreshLayout에서 refresh가 작동할 경우
        mainBinding.refreshLayout.setOnRefreshListener {
            mainBinding.webView.reload()
        }


    }

    // 내부 클래스로 선언해야 상위 클래스에 접근이 가능
    inner class WebViewClient: android.webkit.WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            mainBinding.progressBar.show()  // 페이지 시작할때 progressBar 나타남
        }

        // 페이지가 로딩이 끝났을 경우
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            // 로딩이 끝났을 경우 false로 만들어 줘야 갱신중이라는 UI가 끝이난다
            mainBinding.refreshLayout.isRefreshing = false
            mainBinding.progressBar.hide()    // 페이지 로딩이 끝날경우 숨김
            mainBinding.backBtn.isEnabled = mainBinding.webView.canGoBack()    // 뒤로 갈 수 있는 경우(True)일 경우만 버튼 활성화
            mainBinding.forwardBtn.isEnabled = mainBinding.webView.canGoForward()    // 앞으로 갈 수 있는 경우(True)일 경우만 버튼 활성화
            mainBinding.addressBar.setText(url)    // 최종적으로 로딩된 url로 바꿔준다

        }
    }

    inner class WebChromeClient: android.webkit.WebChromeClient() {


        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            mainBinding.progressBar.progress = newProgress

        }
    }

    companion object {
        private const val HOMEURL = "http://www.google.com"
    }



}