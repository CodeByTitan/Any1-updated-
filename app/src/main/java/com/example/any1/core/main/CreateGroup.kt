package com.example.any1.core.main

import android.Manifest
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.any1.feature_group.domain.adapter.CreateGroupAdapter
import com.bumptech.glide.Glide
import com.example.any1.R
import com.example.any1.feature_login.presentation.SearchTags
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.io.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class CreateGroup : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    var storageReference: StorageReference? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var temporaryname = ""
    private lateinit var filepath : Uri
    var mGetContent: ActivityResultLauncher<String>? = null
    var getImageFromCamera: ActivityResultLauncher<Intent>? = null
    private lateinit var dialog: Dialog
    private var downloadUrl = ""
    private var tagarray = ArrayList<String>()
    private var membersarray = ArrayList<String>()
    private lateinit var picUri : Uri
    private lateinit var tagrecyclerview : RecyclerView
    private lateinit var grouppreferences : SharedPreferences
    private lateinit var creategcadapter : CreateGroupAdapter
    private lateinit var gcname : EditText
    private lateinit var gcnameconfirm : ImageView
    private lateinit var confirmtick: ImageView
    private lateinit var gctagstr : String
    private lateinit var edittags : ImageView
    private lateinit var groupnamestr : String
    private lateinit var groupphoto : ImageView
    private lateinit var approval : SwitchMaterial
    private lateinit var toolbar : Toolbar
    private lateinit var gceditor : SharedPreferences.Editor
    var gcpic = ""
    var savedimgurl = ""
    private var admins = ArrayList<String>()
    private lateinit var tagPreferences : SharedPreferences
    private lateinit var rankupdates : CheckBox
    private lateinit var searchtags: TextView
    var primarytag :String =""
    var secondarytag :String =""
    var tertiarytag :String = ""
    private lateinit var listener : OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        gcname = findViewById(R.id.groupname)
        searchtags = findViewById<TextView>(R.id.tags)
        groupphoto = findViewById(R.id.gcphoto)
        approval = findViewById(R.id.approval)
        rankupdates = findViewById(R.id.rankingupdates)
        filepath = "".toUri()
        grouppreferences = getSharedPreferences(packageName+"group", MODE_PRIVATE)
        tagPreferences = getSharedPreferences(packageName+"tag", MODE_PRIVATE)
        if(intent!=null && intent.getStringExtra("home")=="home"){
            tagPreferences.edit().clear().apply()
            grouppreferences.edit().clear().apply()
        }

        toolbar = findViewById(R.id.phonetoolbar)
        val creategc = findViewById<ImageView>(R.id.creategc)
        gcnameconfirm = findViewById<ImageView>(R.id.gcnameconfirm)
        confirmtick = findViewById<ImageView>(R.id.confirmtick)
        edittags = findViewById(R.id.edittags)
        val gctaginfo = findViewById<TextView>(R.id.gctaginfo)
        var approvalbool: Boolean
        storageReference = FirebaseStorage.getInstance().reference
        tagrecyclerview = findViewById(R.id.tagrecyclerview)
        val sharedPreferences = getSharedPreferences(packageName+"user", MODE_PRIVATE)
        savedimgurl = sharedPreferences.getString("imgurl","").toString()
        gctagstr = grouppreferences.getString("gctag","").toString()
        groupnamestr = grouppreferences.getString("gcname","").toString()
        gcpic = grouppreferences.getString("imgurl","").toString()
        gceditor = grouppreferences.edit()
        toolbar.setNavigationOnClickListener {
            grouppreferences.edit().clear().apply()
            tagPreferences.edit().clear().apply()
            finish()
        }

        listener = OnSharedPreferenceChangeListener { prefs, key ->
                if(key!=null && key.equals("primarytag")){
                    if((prefs.getString("primarytag","")=="") && (prefs.getString("secondarytag","")=="")  && (prefs.getString("tertiarytag","")=="")){
                        edittags.visibility = View.INVISIBLE
                        searchtags.text = "Add Group Search Tags"
                    }
                }
                if(key!=null && key.equals("secondarytag")){
                    if((prefs.getString("primarytag","")=="") && (prefs.getString("secondarytag","")=="")  && (prefs.getString("tertiarytag","")=="")){
                        edittags.visibility = View.INVISIBLE
                        searchtags.text = "Add Group Search Tags"
                    }
                }
                if(key!=null && key.equals("tertiarytag")){
                    if((prefs.getString("primarytag","")=="") && (prefs.getString("secondarytag","")=="")  && (prefs.getString("tertiarytag","")=="")){
                        edittags.visibility = View.INVISIBLE
                        searchtags.text = "Add Group Search Tags"
                    }
                }
            }


        tagPreferences.registerOnSharedPreferenceChangeListener(listener)
        edittags.setOnClickListener {
            tagPreferences.edit().putString("primaryincg",tagPreferences.getString("primarytag","")).apply()
            tagPreferences.edit().putString("secondaryincg",tagPreferences.getString("secondarytag","")).apply()
            tagPreferences.edit().putString("tertiaryincg",tagPreferences.getString("tertiarytag","")).apply()
            val intent = Intent(this, SearchTags::class.java)
            intent.putExtra("donotdelete","donotdelete")
            intent.putExtra("cg","cg")
            val bndlAnimation = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.anim_enter,
                R.anim.anim_exit
            ).toBundle()
            startActivity(intent, bndlAnimation)
        }

        if(grouppreferences.getString("imgurl","")==""){
            if (savedimgurl != "male" && savedimgurl != "female" && savedimgurl != "") {
                Glide.with(this).load(savedimgurl).circleCrop().into(groupphoto)
                gceditor.putString("imgurl", savedimgurl).apply()
            } else {
                gceditor.putString("imgurl", "").apply()
            }
        }

        if(!membersarray.contains(auth.currentUser!!.uid))   membersarray.add(auth.currentUser!!.uid)

        if (grouppreferences.getString("imgurl", "") != "") {
            Glide.with(this).load(grouppreferences.getString("imgurl", "")).circleCrop().into(groupphoto)
        }
        admins.add(auth.currentUser!!.uid)
        creategc.setOnClickListener {
            when {
                grouppreferences.getString("gcname", "") == "" -> {
                    gcname.error = "Group name is a mandatory field"
                }
                grouppreferences.getString("gctag", "") == "" -> {
                    Toast.makeText(this, "Please generate a group tag", Toast.LENGTH_SHORT).show()
                }
                tagarray.isEmpty() -> {
                    Toast.makeText(this, "Please select at least one group search tag", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val dialog3 = Dialog(this)
                    dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog3.setContentView(R.layout.progressbardialog)
                    val pleasewait = dialog3.findViewById<TextView>(R.id.pleasewait)
                    pleasewait.text = "Creating Group"
                    dialog3.show()
                    val hashMap = hashMapOf(
                    "admins" to admins,
                    "requests" to arrayListOf<String>(),
                    "approval" to grouppreferences.getString("approval", "").toBoolean(),
                    "name" to grouppreferences.getString("gcname", ""),
                    "nosimping" to false,
                    "imageurl" to grouppreferences.getString("imgurl", ""),
                    "membercount" to membersarray.size,
                    "searchtag" to tagarray,
                    "members" to membersarray,
                    "accessibility" to "public",
                    "hidetags" to false,
                    "namelock" to true,
                    "videocall" to true,
                    "rankupdates" to grouppreferences.getString("rankupdates","").toBoolean()
                )

                    firestore.collection("groups").document(gctagstr).set(hashMap)
                        .addOnSuccessListener {
                            firestore.collection("users").document(auth.currentUser!!.uid).collection("groups").document(gctagstr).set(
                                hashMapOf("messagemuted" to false)
                            ).addOnSuccessListener {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                grouppreferences.edit().clear().apply()
                                tagPreferences.edit().clear().apply()
                                dialog3.dismiss()
                            }
                        }.addOnFailureListener{
                            Toast.makeText(this, "task unsucessfull", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }


        approval.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener{
            compoundButton, b ->
            approvalbool = b
            gceditor.putString("approval",approvalbool.toString()).apply()
        })

        rankupdates.setOnCheckedChangeListener{
            compoudbutton ,b ->
            if (b) run {
                dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.rankupdatedialog)
                val learnmore = dialog.findViewById<TextView>(R.id.learnmore)
                val ok = dialog.findViewById<TextView>(R.id.ok)
                ok.setOnClickListener{
                    dialog.dismiss()
                }
                learnmore.setOnClickListener{
                    dialog.dismiss()
                    intent = Intent(this,learnmore::class.java)
                    val bndlAnimation = ActivityOptions.makeCustomAnimation(
                        this,
                        R.anim.anim_enter,
                        R.anim.anim_exit
                    ).toBundle()
                    startActivity(intent,bndlAnimation)
                }
                dialog.show()
                gceditor.putString("rankupdates",b.toString()).apply()
            }
        }

        gctaginfo.visibility = View.INVISIBLE

        gcname.doAfterTextChanged {
            temporaryname = it.toString()
            if(temporaryname != ""){
                gcname.error = null
                if(groupnamestr != it.toString()){
                    grouppreferences.edit().putString("gcname",it.toString()).apply()
                    gcnameconfirm.visibility = View.VISIBLE
                    confirmtick.visibility = View.INVISIBLE
                    gceditor.putString("tick","false")
                }else{
                    gcnameconfirm.visibility = View.INVISIBLE
                    confirmtick.visibility = View.VISIBLE
                    gceditor.putString("tick","true")
                }
            }
        }

        groupphoto.setOnClickListener {
            if(gctagstr == ""){
                Toast.makeText(this, "Please generate a group tag first", Toast.LENGTH_SHORT).show()
            }else{
                showDialog()
            }
        }

        searchtags.setOnClickListener{
            tagPreferences.edit().putString("primaryincg",tagPreferences.getString("primarytag","")).apply()
            tagPreferences.edit().putString("secondaryincg",tagPreferences.getString("secondarytag","")).apply()
            tagPreferences.edit().putString("tertiaryincg",tagPreferences.getString("tertiarytag","")).apply()
            val intent = Intent(this, SearchTags::class.java)
            if(searchtags.text != "Add Group Search Tags"){
                tagPreferences.edit().putString("counts","").apply()
                intent.putExtra("donotdelete","donotdelete")
            }
            intent.putExtra("cg","cg")
            val bndlAnimation = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.anim_enter,
                R.anim.anim_exit
            ).toBundle()
            startActivity(intent,bndlAnimation)
        }

        gcnameconfirm.setOnClickListener {
            val temporarytag = generateGcTag()
            checkFirestoreForTag(temporarytag)
            if(temporaryname!= ""){
                if(gctaginfo.visibility== View.INVISIBLE){
                    val dialog2 = Dialog(this)
                    dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog2.setContentView(R.layout.progressbardialog)
                    val pleasewait = dialog2.findViewById<TextView>(R.id.pleasewait)
                    pleasewait.text = "Generating a group tag"
                    dialog2.show()
                    val t = Timer()
                    t.schedule(object : TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                gctaginfo.visibility = View.VISIBLE
                                gctaginfo.text = getString(R.string.gctagstring,gctagstr)
                                confirmtick.visibility = View.VISIBLE
                                gcnameconfirm.visibility = View.INVISIBLE
                                gceditor.putString("gcname",temporaryname).apply()
                                gceditor.putString("tick","true")
                                dialog2.dismiss()
                                t.cancel()
                            }
                        }
                    }, 2000)
                }else{
                    gceditor.putString("gcname",temporaryname).apply()
                    confirmtick.visibility = View.VISIBLE
                    gcnameconfirm.visibility = View.INVISIBLE
                }
            }else{
                gcname.error = "Group Name Cannot Be Empty"
            }
        }
        mGetContent = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { result ->
            if (result != null) {
                dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.progressbardialog)
                dialog.setCancelable(false)
                val pleasewait = dialog.findViewById<TextView>(R.id.pleasewait)
                pleasewait.text = "Setting Group Photo"
                dialog.show()
                uploadToFirebase(result)
            }
        }

        getImageFromCamera = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.data != null) {
                val extras = result.data!!.extras
                if (extras != null) {
                    dialog = Dialog(this)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.progressbardialog)
                    dialog.setCancelable(false)
                    val pleasewait = dialog.findViewById<TextView>(R.id.pleasewait)
                    pleasewait.text = "Setting Group Photo"
                    dialog.show()
                    val imageBitmap = extras["data"] as Bitmap?
                    filepath = saveQualityImage(imageBitmap!!,this)!!
                    uploadToFirebase(filepath)
                }
            }
        }
    }

    override fun onStop() {
        tagPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        tagPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    override fun onBackPressed() {
        grouppreferences.edit().clear().apply()
        tagPreferences.edit().clear().apply()
        super.onBackPressed()
    }


    override fun onNewIntent(intent: Intent?) {
        val extras = intent!!.extras
        if(extras!!.containsKey("cancel")){
            gcname.setText(grouppreferences.getString("gcname", ""))
            if (grouppreferences.getString("approval", "") == "true") approval.isChecked =
                true
            val primaryincg = tagPreferences.getString("primaryincg","").toString()
            val secondaryincg = tagPreferences.getString("secondaryincg","").toString()
            val tertiaryincg = tagPreferences.getString("tertiaryincg","").toString()
            temporaryname = grouppreferences.getString("gcname", "").toString()
            primarytag = tagPreferences.getString("primarytag","").toString()
            secondarytag = tagPreferences.getString("secondarytag","").toString()
//            Toast.makeText(this, grouppreferences.getString("imgurl",""), Toast.LENGTH_SHORT).show()
//            Picasso.get().load((grouppreferences.getString("imgurl",""))).transform(CircleTransform()).into(groupphoto)
            tertiarytag = tagPreferences.getString("tertiarytag","").toString()
            tagPreferences.edit().putString("primarytag",primaryincg).apply()
            tagPreferences.edit().putString("secondarytag",secondaryincg).apply()
            tagPreferences.edit().putString("tertiarytag",tertiaryincg).apply()
            if(grouppreferences.getString("tick","")=="true"){
                confirmtick.visibility = View.VISIBLE
                gcnameconfirm.visibility = View.INVISIBLE
            }else{
                confirmtick.visibility = View.INVISIBLE
                gcnameconfirm.visibility = View.VISIBLE
            }
            Glide.with(this).load(grouppreferences.getString("imgurl","")).circleCrop().into(groupphoto)
            creategcadapter = CreateGroupAdapter(tagarray,this)
            tagrecyclerview.adapter = creategcadapter
        }else{
            primarytag = tagPreferences.getString("primarytag","").toString()
            secondarytag = tagPreferences.getString("secondarytag","").toString()
            tertiarytag = tagPreferences.getString("tertiarytag","").toString()
            val primaryincg = tagPreferences.getString("primaryincg","")
            val secondaryincg = tagPreferences.getString("secondaryincg","")
            val tertiaryincg = tagPreferences.getString("tertiaryincg","")
            if(primarytag!=""){
                if(primaryincg != primarytag){
                    tagarray.remove(primaryincg)
                    tagarray.add(primarytag)
                }
            }else{
                tagarray.remove(primaryincg)
            }
            if(secondarytag !="") {
                if(secondaryincg != secondarytag){
                    tagarray.remove(secondaryincg)
                    tagarray.add(secondarytag)
                }
            }else{
                tagarray.remove(secondaryincg)
            }
            if(tertiarytag != "") {
                if(tertiaryincg != tertiarytag){
                    tagarray.remove(tertiaryincg)
                    tagarray.add(tertiarytag)
                }
            }else{
                tagarray.remove(tertiaryincg)
            }
            if (grouppreferences.getString("approval", "") == "true") approval.isChecked =
                true
            temporaryname = grouppreferences.getString("gcname", "").toString()
//            Glide.with(this).load(grouppreferences.getString("imgurl","")).circleCrop().into(groupphoto)
            if(grouppreferences.getString("imgurl","")!="")Picasso.get().load((grouppreferences.getString("imgurl",""))).transform(
                CircleTransform()
            ).into(groupphoto)
            tagrecyclerview.visibility = View.VISIBLE
            searchtags.text = "Group Search Tags"
            edittags.visibility = View.VISIBLE
            tagrecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            creategcadapter = CreateGroupAdapter(tagarray,this)
            tagrecyclerview.adapter = creategcadapter
            if(primarytag!=""){
                tagPreferences.edit().putString("primaryvisible","true").apply()
                tagPreferences.edit().putString("primaryincg",primarytag).apply()
            }else{
                tagPreferences.edit().putString("primaryincg","").apply()
            }
            if(secondarytag !="") {
                tagPreferences.edit().putString("secondaryvisible","true").apply()
                tagPreferences.edit().putString("secondaryincg",secondarytag).apply()
            }else{
                tagPreferences.edit().putString("secondaryincg","").apply()
            }
            if(tertiarytag !=""){
                tagPreferences.edit().putString("tertiaryvisible","true").apply()
                tagPreferences.edit().putString("tertiaryincg",tertiarytag).apply()
            }else{
                tagPreferences.edit().putString("tertiaryincg","").apply()
            }
        }
        super.onNewIntent(intent)
    }

    private fun generateGcTag(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        val salt = StringBuilder()
        val rnd = Random()
        var a = 6
        while (a > 0) { // length of the random string.
            salt.append(chars[(rnd.nextInt(chars.length))])
            a--
        }
        val saltstr = salt.toString()
        Log.d("tagxd",saltstr)
        return "#$saltstr"
    }
    class CircleTransform : Transformation {
        override fun transform(source: Bitmap): Bitmap {
            val size = Math.min(source.width, source.height)
            val x = (source.width - size) / 2
            val y = (source.height - size) / 2
            val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
            if (squaredBitmap != source) {
                source.recycle()
            }
            val bitmap = Bitmap.createBitmap(size, size, source.config)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            val shader = BitmapShader(
                squaredBitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP
            )
            paint.shader = shader
            paint.isAntiAlias = true
            val r = size / 2f
            canvas.drawCircle(r, r, r, paint)
            squaredBitmap.recycle()
            return bitmap
        }

        override fun key(): String {
            return "circle"
        }
    }

//    private fun generateGcTag(): String {
//        val buffer = StringBuffer()
//        while (buffer.length < 6) {
//            buffer.append(uuidString())
//        }
//        Toast.makeText(this, buffer.toString(), Toast.LENGTH_SHORT).show()
//        //this part controls the length of the returned string
//        return buffer.substring(0,6)
//    }
//
//
//    private fun uuidString(): String? {
//        return UUID.randomUUID().toString().replace("-".toRegex(), "")
//    }
//    private fun generateGcTag() {
//        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
//        val str = "#" + (1..6)
//            .map { charset.random() }
//            .joinToString("")
//        checkFirestoreForTag(str)
//    }

    private fun uploadToFirebase(uri: Uri?) {
        if (uri != null) {
            val fileRef: StorageReference = storageReference?.child("groupImages")!!.child("$gctagstr.jpeg")
            val uploadTask = fileRef.putFile(uri)
            //        Task<Uri> uriTask=
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                downloadUrl = fileRef.downloadUrl.toString()
                fileRef.downloadUrl
            }.addOnCompleteListener { task ->
                picUri = task.result
                downloadUrl = picUri.toString()
                grouppreferences.edit().putString("imgurl",downloadUrl).apply()
                filepath = picUri
                Glide.with(this).load(picUri).circleCrop().into(groupphoto)
                dialog.dismiss()
            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Uploading Failed !!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    fun removeDuplicates(a: ArrayList<String>): ArrayList<String> {
        val set = HashSet<String>()
        for (i in a){
            set.add(i)
        }
        return set.toCollection(a)
    }
    private fun checkFirestoreForTag(str : String){
//        Toast.makeText(this, str+"ayooo", Toast.LENGTH_SHORT).show()
        firestore.collection("groups").document(str).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
//                val temptag = generateGcTag()
//                checkFirestoreForTag(temptag)
//                Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
                grouppreferences.edit().putString("gctag",str).apply()
                gctagstr = str
//                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
            }else{
                grouppreferences.edit().putString("gctag",str).apply()
                gctagstr = str
//                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveQualityImage(bm: Bitmap, context: Context): Uri? {
        val imagesFolder = File(context.cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, auth.currentUser!!.uid + ".jpeg")
            val stream = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(
                context.applicationContext,
                "com.example.any1" + ".provider",
                file
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return uri
    }

    private val askcamerapermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(
                this,
                "We require permission to access camera of your device in order to set your profile photo." +
                        " To setup your profile photo, go to Settings-> Apps -> Stucon -> Permissions -> grant camera permission",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openCamera() {
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        getImageFromCamera!!.launch(camera)
    }

    override fun onResume() {
        tagPreferences.registerOnSharedPreferenceChangeListener(listener)
        val prefs = getSharedPreferences(packageName+"tag", MODE_PRIVATE)
        if(prefs.getString("primarytag","")==""){
            edittags.visibility = View.INVISIBLE
            searchtags.text = "Add Group Search Tags"
        }
        if(!membersarray.contains(auth.currentUser!!.uid))   membersarray.add(auth.currentUser!!.uid)
        if(grouppreferences.getString("tick","")=="true"){
            confirmtick.visibility = View.VISIBLE
            gcnameconfirm.visibility = View.INVISIBLE
        }else{
            confirmtick.visibility = View.INVISIBLE
            gcnameconfirm.visibility = View.VISIBLE
        }
        if(grouppreferences.getString("imgurl","")==""){
            if (savedimgurl != "male" && savedimgurl != "female" && savedimgurl != "") {
                Glide.with(this).load(savedimgurl).circleCrop().into(groupphoto)
                gceditor.putString("imgurl", savedimgurl).apply()
            } else {
                gceditor.putString("imgurl", "").apply()
            }
        }else{
            Glide.with(this).load(grouppreferences.getString("imgurl","")).circleCrop().into(groupphoto)
        }
        super.onResume()
    }

    private val askgallerypermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            mGetContent!!.launch("image/*")
        } else {
            Toast.makeText(
                this,
                "We require permission to access files and media on your device in order to set your profile photo."
                        + " To setup your profile photo, go to Settings-> Apps -> Any1 -> Permissions -> grant Files and media permission",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.pfpdialog)
        val camera = dialog.findViewById<TextView>(R.id.camera)
        val gallery = dialog.findViewById<TextView>(R.id.materialTextView2)
        camera?.setOnClickListener {
            dialog.dismiss()
            Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        openCamera()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        askcamerapermission.launch(Manifest.permission.CAMERA)
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }
        gallery?.setOnClickListener {
            dialog.dismiss()
            Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        mGetContent!!.launch("image/*")
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        askgallerypermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }
        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.Dialoganimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }

}