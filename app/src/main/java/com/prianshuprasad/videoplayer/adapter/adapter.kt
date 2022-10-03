package com.prianshuprasad.videoplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.prianshuprasad.videoplayer.MainActivity
import com.prianshuprasad.videoplayer.R
import com.prianshuprasad.videoplayer.videoData

class adapter(private val listener: MainActivity) :
    RecyclerView.Adapter<adapter.ViewHolder>() {


    private val item: ArrayList<videoData> = ArrayList() // array to store videoData. VideoData stores url
    private val playerList:ArrayList<ExoPlayer> = ArrayList() // array to store Exoplyer which is later used to control playback of videos on scroll

    //ViewHolder Class
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val videoPlayer:PlayerView

        init {
            //initializing videoPlayer variable
            videoPlayer= view.findViewById(R.id.player1)

        }




    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_view, viewGroup, false)


        val viewHolder= ViewHolder(view)
        view.setOnClickListener {

            // index is the viewHolder position which is clicked
            val index=viewHolder.adapterPosition

//            if video is playing then pause it else play it
            if(playerList[index].isPlaying){
                playerList[index].pause()

            }else
            {
                playerList[index].play()

            }


        }


        return viewHolder
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {


        // declaring and initilizing Exoplayer
        val player = ExoPlayer.Builder(listener).build()

        viewHolder.videoPlayer.setPlayer(player)

        player.setRepeatMode(Player.REPEAT_MODE_ONE)

        val mediaItem: MediaItem = MediaItem.fromUri(item[position].url)

        // resezieMode enables centreCrop like video
        viewHolder.videoPlayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
        player.setMediaItem(mediaItem)

        //prepare thr player
        player.prepare()
        // Start the playback.

        player.setForegroundMode(true)

        // adding the player in playerList
        playerList.add(position,player)





    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = item.size


    // the function is called from MainActivity if there is any data change
    fun update(array:ArrayList<videoData>){


        item.clear()
        item.addAll(array)

        notifyDataSetChanged()

    }


   fun videoControl_ChangePage(currPosition:Int , prevPosition:Int){
       // Pause the playback for prevIndex and play for currIndex

       playerList[prevPosition].pause()
       playerList[currPosition].play()

   }

    fun videoControl_OnScroll(prevPosition: Int){
        // pause the playback when scrolling state
        playerList[prevPosition].pause()

    }

    fun videoControll_OnFalseScroll(currPosition: Int){

//        replay the prev video if false scroll is detected
        playerList[currPosition].play()


    }





}
