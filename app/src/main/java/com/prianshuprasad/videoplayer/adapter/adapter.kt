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


    private val item: ArrayList<videoData> = ArrayList()
    private val playerList:ArrayList<ExoPlayer> = ArrayList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val videoPlayer:PlayerView

        init {

            videoPlayer= view.findViewById(R.id.player1)

        }




    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_view, viewGroup, false)


        val viewHolder= ViewHolder(view)
        view.setOnClickListener {

            val index=viewHolder.adapterPosition

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





        val player = ExoPlayer.Builder(listener).build()

        viewHolder.videoPlayer.setPlayer(player)

        player.setRepeatMode(Player.REPEAT_MODE_ONE)




        val mediaItem: MediaItem = MediaItem.fromUri(item[position].url)

        viewHolder.videoPlayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
        player.setMediaItem(mediaItem)

        player.prepare()
// Start the playback.
// Start the playback.

        player.setForegroundMode(true)

        playerList.add(position,player)





    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = item.size


    fun update(array:ArrayList<videoData>){

        item.clear()
        item.addAll(array)

        notifyDataSetChanged()

    }


   fun videoControl_ChangePage(currPosition:Int , prevPosition:Int){


       playerList[prevPosition].pause()
       playerList[currPosition].play()

   }

    fun videoControl_OnScroll(prevPosition: Int){
        playerList[prevPosition].pause()

    }

    fun videoControll_OnFalseScroll(currPosition: Int){
        playerList[currPosition].play()


    }





}
