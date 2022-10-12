package com.prianshuprasad.videoplayer



import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.Task
import com.google.firebase.database.annotations.Nullable
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.UploadTask
import com.prianshuprasad.videoplayer.adapter.adapter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mAdapter: adapter  // adapter
    lateinit var rcview:ViewPager2  // viewPager2
    var currentPos:Int=0  // variable storing the current index of page in ViewPager2
    var prevPosition:Int=0 //  variable storing the previous index of page in ViewPager2
    private var videouri: Uri? = null
    private lateinit var database:dataBase
    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        database= dataBase()
        rcview= findViewById(R.id.viewPagerVideos)
        progressDialog =  ProgressDialog(this);
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
                positionOffsetPixels: Int,
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

  addButton.setOnClickListener {

      chooseDailogue()
  }

        searchBox.setOnEditorActionListener(object :
            View.OnFocusChangeListener, TextView.OnEditorActionListener {

            override fun onFocusChange(view: View?, hasFocus: Boolean) = Unit

            override fun onEditorAction(
                textView: TextView,
                actionId: Int,
                event: KeyEvent?,
            ): Boolean {

                val searchStr= searchBox.text.toString()
                if(searchStr=="" || searchStr==" ")
                fetch()
                else
                    fetch(searchStr)
                return true
            }
        })




//        // to be removed
//        var arr= arrayListOf("home","rp")
//        database.uploadData("https://cdn.pixabay.com/vimeo/328940142/Buttercups%20-%2022634.mp4?width=1304&hash=2df4ff27ac821dcb2174355e8051bd782697fcb4","Hello",arr)


    }

    fun chooseDailogue(){

        val li = LayoutInflater.from(this)
        val promptsView: View = li.inflate(com.prianshuprasad.videoplayer.R.layout.prompt_add, null)

        val camera:ImageButton= promptsView.findViewById(R.id.cameraButton)
        val files:ImageButton= promptsView.findViewById(R.id.filesButton)





        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

        // set prompts.xml to alertdialog builder

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView)

        // set dialog message

        // set dialog message
        alertDialogBuilder
            .setCancelable(false)

            .setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

        // create alert dialog

        // create alert dialog
        val alertDialog: AlertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()

        camera.setOnClickListener {
            val camera_intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(camera_intent, 5)
          alertDialog.cancel()

        }
        files.setOnClickListener {
            choosevideo()
            alertDialog.cancel()
        }



    }


    fun fetch(tagi:String){
         val tag= tagi.lowercase()
        Toast.makeText(this,"Showing result for $tag",Toast.LENGTH_LONG).show()
        val array:ArrayList<videoData> = ArrayList()
        val db = database.getSearchHelper().getDB()
        db.document(tag).get().addOnCompleteListener {

            if( it.getResult().exists()){

                val arr= it.getResult().toObject(searchData::class.java)!!.arrayVideoID

                val videoCollection= database.getDB()

                for(docid in arr){

                    videoCollection.document(docid).get().addOnCompleteListener {
                        if(it.getResult().exists()){
                            val vdata:videoData= it.getResult().toObject(videoData::class.java)!!
                            array.add(vdata)

                             if(array.size==arr.size){

                                mAdapter.update(array)

                                 Toast.makeText(this@MainActivity,"${array.size}",Toast.LENGTH_LONG).show()

                             }


                        }
                    }

                }





            }

        }



    }


   fun fetch(){


       val array:ArrayList<videoData> = ArrayList()
       val collection= database.getDB()

      collection.get().addOnCompleteListener {
          if(!it.getResult().isEmpty){
              for(document in it.result!!){


//                  Toast.makeText(this@MainActivity,document.toString(),Toast.LENGTH_LONG).show()
                  val vdata:videoData=document.toObject(videoData::class.java)
//                  Toast.makeText(this@MainActivity,"${vdata.url}",Toast.LENGTH_LONG).show()
                  array.add(vdata)
              }
              mAdapter.update(array)
          }else
              Toast.makeText(this@MainActivity,"No Data To Show",Toast.LENGTH_LONG).show()
      }



   }

    // choose a video from phone storage
    private fun choosevideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 5)
    }



    private fun getfiletype(videouri: Uri): String? {
        val r = contentResolver
        // get the file type ,in this case its mp4
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri))
    }



    private fun uploadvideo(videoTitle:String,tags:ArrayList<String>) {


        progressDialog?.setTitle("Uploading...")
        progressDialog?.show()

        if (videouri != null) {
            // save the selected video in Firebase storage
            val reference = FirebaseStorage.getInstance()
                .getReference("Files/" + System.currentTimeMillis() + "." + getfiletype(
                    videouri!!))
            reference.putFile(videouri!!).addOnSuccessListener { taskSnapshot ->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful());
                // get the link of video
                val downloadUri: String = uriTask.getResult().toString()

//                val reference1: DatabaseReference =
//                    FirebaseDatabase.getInstance().getReference("Video")

               database.uploadData(downloadUri,videoTitle,tags)

//                val map: HashMap<String, String> = HashMap()
//                map["videolink"] = downloadUri
//                reference1.child("" + System.currentTimeMillis()).setValue(map)
                // Video uploaded successfully
                // Dismiss dialog
                progressDialog?.dismiss()
                Toast.makeText(this@MainActivity, "Video Uploaded!!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e -> // Error, Image not uploaded
                progressDialog?.dismiss()
                Toast.makeText(this@MainActivity, "Failed " + e.message, Toast.LENGTH_SHORT).show()
            }.addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot?> {
                // Progress Listener for loading
                // percentage on the dialog box
                override fun onProgress(taskSnapshot: UploadTask.TaskSnapshot) {
                    // show the progress bar
                    val progress =
                        100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    progressDialog?.setMessage("Uploaded " + progress.toInt() + "%")
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 5 && resultCode == RESULT_OK && data != null && data.data != null) {
            videouri = data.data

            var durationTime: Long
            MediaPlayer.create(this, videouri).also {
                durationTime = (it.duration / 1000).toLong()
                if(durationTime>60)
                {
                    Toast.makeText(this@MainActivity,"PLease select a video with max duration of 1 minute",Toast.LENGTH_LONG).show()
                }else
                    askTitle()

                it.reset()
                it.release()
            }


        }
    }

    fun askTitle(){
        var videoTitle=""

        val li = LayoutInflater.from(this)
        val promptsView: View = li.inflate(com.prianshuprasad.videoplayer.R.layout.prompt, null)

        val editText:EditText= promptsView.findViewById(R.id.videoTitle)
        val editText1:EditText= promptsView.findViewById(R.id.videoTag)

        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

        // set prompts.xml to alertdialog builder

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView)

        // set dialog message

        // set dialog message
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("Upload",
                DialogInterface.OnClickListener { dialog, id -> // get user input and set it to result
                    // edit text
//                    result.setText(promptsView
//                        .findViewById(android.R.id.editTextDialogUserInput).text)
                    videoTitle= editText.text.toString()
                    val tagsStr= editText1.text.toString()
                    val tagArr= tagsStr.split(',',' ')
                    uploadvideo(videoTitle,tagArr as ArrayList)

                })
            .setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

        // create alert dialog

        // create alert dialog
        val alertDialog: AlertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()



    }




}