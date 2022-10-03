package com.prianshuprasad.videoplayer



import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.prianshuprasad.videoplayer.adapter.adapter


class MainActivity : AppCompatActivity() {

    private lateinit var mAdapter: adapter
    lateinit var rcview:ViewPager2
    var currentPos:Int=0
    var prevPosition:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        rcview= findViewById(R.id.viewPagerVideos)

        mAdapter=  adapter(this)
        rcview.adapter= mAdapter
        fetch()





        rcview.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

               currentPos= position

                mAdapter.videoControl_ChangePage(currentPos,prevPosition)

                prevPosition= currentPos


            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                if(state!=0)
                mAdapter.videoControl_OnScroll(prevPosition)
                else
                    mAdapter.videoControll_OnFalseScroll(currentPos)
            }
        })







    }


    fun fetch(): MutableList<videoData> {

        val dataArray= mutableListOf<videoData>()



        val url ="https://pixabay.com/api/videos/?key=30305956-0e86d639df1a301cd153ad39b&q=yellow+flowers&image_type=photo&pretty=true"
        val mRequestQueue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,

            { response ->


                val hintsArray = response.getJSONArray("hits")

                for(i in 0..(hintsArray.length()) ){

                    try {
                        val video_url = hintsArray.getJSONObject(i).getJSONObject("videos")
                            .getJSONObject("tiny").getString("url")

                        dataArray.add(videoData(video_url))


                    }catch (e:Exception ){



                    }


                }


                mAdapter.update(dataArray as ArrayList<videoData>)




            },
            { error ->

                Toast.makeText(this,"Some Error Occured",Toast.LENGTH_LONG).show()
            }
        )
        mRequestQueue.add(jsonObjectRequest)
       return dataArray
    }





}