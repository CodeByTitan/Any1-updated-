package com.example.any1.feature_signup.presentation

import android.Manifest
import android.app.Dialog
import android.content.*
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.any1.feature_group.domain.vm.GroupVM
import com.bumptech.glide.Glide
import com.example.any1.R
import com.example.any1.core.main.MainActivity
import com.example.any1.feature_signup.domain.AddAccountViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [displaynamepic.newInstance] factory method to
 * create an instance of this fragment.
 */
class Pic : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    var firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    var downloadUrl: String? = null
    var picUri: Uri? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    lateinit var next:Button
    var storageReference: StorageReference? = null
    private lateinit var pfp : ImageView
    private lateinit var dialog :Dialog
    lateinit var skip: TextView
    private lateinit var imageStream : InputStream
    private lateinit var bitmap : Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    var mGetContent: ActivityResultLauncher<String>? = null
    var getImageFromCamera: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_displaynamepic, container, false)
        val pfpset = view.findViewById<TextView>(R.id.pfpset)
        pfp = view.findViewById<ImageView>(R.id.pfp)
        next = view.findViewById<Button>(R.id.pfpnext)
        sharedPreferences = requireContext().getSharedPreferences(requireContext().packageName+"temporaryuser",MODE_PRIVATE)
        val gender = sharedPreferences!!.getString("gender","")
        val addAccountViewModel = ViewModelProvider(requireActivity()).get(AddAccountViewModel::class.java)
        if(gender== "male"){
            pfpset.text = getString(R.string.welcomeking)
             imageStream = this.resources.openRawResource(R.raw.gigachad)
             bitmap = BitmapFactory.decodeStream(imageStream)
            pfp.setImageBitmap(bitmap)
        }else{
            pfpset.text = getString(R.string.welcomequeen)
             imageStream = this.resources.openRawResource(R.raw.doomergirl)
             bitmap = BitmapFactory.decodeStream(imageStream)
             pfp.setImageBitmap(bitmap)
        }
        storageReference = FirebaseStorage.getInstance().reference
        next.setOnClickListener {
            val shp = requireContext().getSharedPreferences(requireContext().packageName+"login", MODE_PRIVATE)
            val age = sharedPreferences!!.getString("age","")?.toInt()
            val birthdate = sharedPreferences!!.getString("birthdate","")
            val username = sharedPreferences!!.getString("username","")
            val name = sharedPreferences!!.getString("displayname","")
            val bm = (pfp.drawable as BitmapDrawable).bitmap
            if( bitmap == bm && gender == "male"){
                sharedPreferences!!.edit().putString("imgurl","male").apply()
            }else if(bitmap == bm && gender == "female") {
                sharedPreferences!!.edit().putString("imgurl","female").apply()
            }
            val firestore = FirebaseFirestore.getInstance()
            val emailid = sharedPreferences!!.getString("email","")
            val documentReference: DocumentReference = firestore.collection("users").document(firebaseAuth.currentUser!!.uid)
            var imgurl = ""
            imgurl = if(sharedPreferences!!.getString("imgurl","")!=""){
                sharedPreferences!!.getString("imgurl","").toString()
            }else{
                gender.toString()
            }
            val hashMap = hashMapOf(
                "displayname" to name,
                "age" to age,
                "birthdate" to birthdate,
                "username" to username,
                "imageurl" to imgurl,
                "gender" to gender,
                "email" to sharedPreferences!!.getString("email","")
            )
            val usernameref = firestore.collection("usernames").document(username.toString())
            usernameref.set(hashMapOf(
                "email" to emailid))


            if(requireActivity().intent.getStringExtra("add")=="add"){
                val sharedPreferences = requireContext().getSharedPreferences(requireContext().packageName+"addedaccounts", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                var numberofaddedaccounts = 0
                val sp = requireContext().getSharedPreferences(requireContext().packageName+"temporaryuser", MODE_PRIVATE)
                if(sharedPreferences.getString("count","")!="") numberofaddedaccounts = sharedPreferences.getString("count","")!!.toInt()
                val accountnumber = numberofaddedaccounts + 1
                editor.putString("count",accountnumber.toString()).apply()
                editor.putString("username$accountnumber",sp.getString("username",""))
                editor.putString("email$accountnumber",sp.getString("email",""))
                editor.putString("imgurl$accountnumber",imgurl)
                editor.putString("displayname$accountnumber",sp.getString("displayname",""))
                editor.putString("age$accountnumber",sp.getString("age",""))
                editor.putString("gender$accountnumber",sp.getString("gender",""))
                editor.putString("password$accountnumber",sp.getString("password",""))
                editor.apply()
            }
            val userpreferences = requireContext().getSharedPreferences(requireContext().packageName+"user", MODE_PRIVATE)
            sharedPreferences = requireContext().getSharedPreferences(requireContext().packageName+"temporaryuser", MODE_PRIVATE)
            val editor = userpreferences.edit()
            editor.putString("username",sharedPreferences!!.getString("username",""))
            editor.putString("email",sharedPreferences!!.getString("email",""))
            editor.putString("imgurl",imgurl)
            editor.putString("displayname",sharedPreferences!!.getString("displayname",""))
            editor.putString("age",sharedPreferences!!.getString("age",""))
            editor.putString("gender",sharedPreferences!!.getString("gender",""))
            editor.putString("password",sharedPreferences!!.getString("password",""))
            editor.apply()

            documentReference.set(hashMap).addOnSuccessListener {
                sharedPreferences!!.edit().clear().apply()
                val auth = FirebaseAuth.getInstance()
                if(requireActivity().intent.getStringExtra("add")=="add") {
                    val viewModel = ViewModelProvider(this).get(GroupVM::class.java)
                    viewModel.updateAuthId(auth.currentUser!!.uid)
                    startActivity(
                        Intent(
                            requireActivity(),
                            MainActivity::class.java
                        )
                    )
                }else{
                    startActivity(Intent(requireActivity(), MainActivity::class.java).setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION))
                }
                shp.edit().putString("login","true").apply()
                requireActivity().finish()
            }
        }

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.profiledialog)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val stopcreation = dialog.findViewById<TextView>(R.id.stopcreating)
                val continuee = dialog.findViewById<TextView>(R.id.continuee)
                continuee.setOnClickListener { dialog.dismiss() }
                stopcreation.setOnClickListener {
                    dialog.dismiss()
                    val username = sharedPreferences?.getString("username", "")
                    if (username != null) {
                        val firestore = FirebaseFirestore.getInstance()
                        firestore.collection("usernames").document(username).get()
                            .addOnSuccessListener {
                                firestore.collection("usernames").document(username).delete()
                                firebaseAuth.currentUser?.delete()
                            }
                    }
                    requireActivity().finish()

                }
                dialog.show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)
        pfp.setOnClickListener{
            showDialog()
        }

        mGetContent =registerForActivityResult(
            ActivityResultContracts.GetContent()
        ){ result ->
            dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.progressbardialog)
            dialog.setCancelable(false)
            val pleasewait = dialog.findViewById<TextView>(R.id.pleasewait)
            pleasewait.text = "Setting Your Pfp"
            dialog.show()
            if(result!=null){
                Glide.with(requireContext()).load(result).circleCrop().into(pfp)
                uploadToFirebase(result)
            }
            dialog.dismiss()

        }

        getImageFromCamera = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.data != null) {
                val extras = result.data!!.extras
                if (extras != null) {
                    dialog = Dialog(requireContext())
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.progressbardialog)
                    dialog.setCancelable(false)
                    val pleasewait = dialog.findViewById<TextView>(R.id.pleasewait)
                    pleasewait.text = "Setting Your Profile Pic"
                    dialog.show()
                    val imageBitmap = extras["data"] as Bitmap?
                    val filepath = context?.let { saveQualityImage(imageBitmap!!, it) }
                    uploadToFirebase(filepath)
                }
            }
        }

        return view
    }

    private fun uploadToFirebase(uri: Uri?) {
        if (uri != null) {
            val uId: String = firebaseAuth.currentUser!!.uid
            val fileRef: StorageReference = storageReference?.child("profileImages")!!.child("$uId.jpeg")
            val uploadTask = fileRef.putFile(uri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                downloadUrl = fileRef.downloadUrl.toString()
                fileRef.downloadUrl
            }.addOnCompleteListener { task ->
                picUri = task.result
                downloadUrl = picUri.toString()
                editor = sharedPreferences!!.edit()
                editor?.putString("imgurl", downloadUrl)
                editor?.apply()
                Glide.with(requireContext()).load(downloadUrl).circleCrop().into(pfp)
            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "Uploading Failed !!",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
        }
    }

    private fun createImage(): Uri{
        var uri : Uri? = null
        val resolver = requireContext().contentResolver
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }else{
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val imageName = System.currentTimeMillis().toString()
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,"$imageName.jpg")
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/"+"Any1/")
        val finalUri : Uri = resolver.insert(uri,contentValues)!!
        return finalUri
    }
    private fun saveQualityImage(bm: Bitmap, context: Context): Uri? {
        val imagesFolder = File(context.cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, firebaseAuth.currentUser!!.uid + ".jpeg")
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
        RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(
                context,
                "We require permission to access camera of your device in order to set your profile photo." +
                        " To setup your profile photo, go to Settings-> Apps -> Any1 -> Permissions -> grant camera permission",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openCamera() {
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imagePath = createImage()
        camera.putExtra(MediaStore.EXTRA_OUTPUT,imagePath)
        getImageFromCamera!!.launch(camera)
    }

    private val askgallerypermission = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
           launchGalleryIntent()
        } else {
            Toast.makeText(
                context,
                "We require permission to access files and media on your device in order to set your profile photo."
                        + " To setup your profile photo, go to Settings-> Apps -> Any1 -> Permissions -> grant Files and media permission",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun launchGalleryIntent(){
        mGetContent!!.launch("image/*")
    }
    private fun getBytesFromBitmap(bitmap: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, 70, stream)
        return stream.toByteArray()
    }


    private fun saveBitmapToFile(
        bm: Bitmap,
    ): Boolean {
        val path = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$path/Any1")
        myDir.mkdirs()
        val imageFile = File(myDir, firebaseAuth.currentUser!!.uid)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(imageFile)
            fos.write(getBytesFromBitmap(bm));
//            bm.compress(format,100, fos)
            fos.close()
            return true
        } catch (e: IOException) {
            Log.e("app", e.message!!)
            if (fos != null) {
                try {
                    fos.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }
            }
        }
        return false
    }
    private fun showDialog() {
        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.pfpdialog)
        val camera = dialog?.findViewById<TextView>(R.id.camera)
        val gallery = dialog?.findViewById<TextView>(R.id.materialTextView2)
        camera?.setOnClickListener {
            dialog.dismiss()
            Dexter.withContext(context)
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
            Dexter.withContext(context)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        launchGalleryIntent()
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
        dialog?.show()
        dialog?.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.Dialoganimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }
    fun getRawUri(filename: String): Uri? {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + File.pathSeparator + File.separator + "com.any1.chat" + "/raw/" + filename)
    }

}