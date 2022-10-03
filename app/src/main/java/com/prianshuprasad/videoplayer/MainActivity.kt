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

    private lateinit var mAdapter: adapter  // adapter
    lateinit var rcview:ViewPager2  // viewPager2
    var currentPos:Int=0  // variable storing the current index of page in ViewPager2
    var prevPosition:Int=0 //  variable storing the previous index of page in ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        rcview= findViewById(R.id.viewPagerVideos)

        // creating an object of adapter
        mAdapter=  adapter(this)

        rcview.adapter= mAdapter

        //calling fetch function to retrieve data from API
        fetch()


            // using registerOnPageChangeCallback() to get page scroll state
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

                //current position is the index at which exoplayer is to played
               currentPos= position

                // At prevPosition index exoplyer is paused
                mAdapter.videoControl_ChangePage(currentPos,prevPosition)

                prevPosition= currentPos


            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                // This method gives gives the scrolling state hence at scrolling state current video should be paused

                if(state!=0)
                mAdapter.videoControl_OnScroll(prevPosition)
                else // at state=0  its not scrolling anymore
                    mAdapter.videoControll_OnFalseScroll(currentPos)

            }
        })







    }


    fun fetch(): MutableList<videoData> {

        val dataArray= mutableListOf<videoData>() // array to store array on videoData . videoData stores url of video

        //api url
        val url ="https://pixabay.com/api/videos/?key=30305956-0e86d639df1a301cd153ad39b&q=yellow+flowers&image_type=photo&pretty=true"

        // volley
        val mRequestQueue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,

            { response ->

                //responce from volley

                val hintsArray = response.getJSONArray("hits")

                for(i in 0..(hintsArray.length()) ){

                    try {

                        //video urls are extracted from Json Object
                        val video_url = hintsArray.getJSONObject(i).getJSONObject("videos")
                            .getJSONObject("tiny").getString("url")

                        dataArray.add(videoData(video_url))


                    }catch (e:Exception ){



                    }


                }


                // data is updated in adapter
                mAdapter.update(dataArray as ArrayList<videoData>)




            },
            { error ->

                //volley could not fetch data from api
                Toast.makeText(this,"Some Error Occured",Toast.LENGTH_LONG).show()
            }
        )
        mRequestQueue.add(jsonObjectRequest)
       return dataArray
    }





}