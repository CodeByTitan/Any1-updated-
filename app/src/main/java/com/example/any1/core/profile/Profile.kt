package com.example.any1.core.profile

import android.app.ActivityOptions
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.any1.feature_signup.presentation.Setup
import com.example.any1.util.dialogs.BottomSheetDialogBuilder
import com.example.any1.util.dialogs.ProgressBarDialogBuilder
import com.example.any1.util.permissions.PermissionGrantedListener
import com.example.any1.util.permissions.PermissionManager
import com.example.any1.feature_group.domain.vm.GroupVM
import com.bumptech.glide.Glide
import com.example.any1.R
import com.example.any1.core.main.MainActivity
import com.example.any1.core.settings.Settings
import com.example.any1.feature_login.domain.adapters.ChangeAccountAdapter
import com.example.any1.feature_login.domain.interfaces.AccountClickListener
import com.example.any1.feature_login.domain.model.SavedAccountModel
import com.example.any1.feature_login.presentation.Login
import com.example.any1.util.sharedpref.SharedPreferenceHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment(), AccountClickListener, PermissionGrantedListener {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var imageStream: InputStream
    private lateinit var bitmap: Bitmap
    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var downloadUrl: String? = null
    var picUri: Uri? = null
    var imageurl : String = ""
    private lateinit var pfp : ImageView
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    var storageReference: StorageReference? = null
    private lateinit var dialog: Dialog
    var mGetContent: ActivityResultLauncher<String>? = null
    private lateinit var editprofile : ImageView
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    var getImageFromCamera: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        pfp = view.findViewById<ImageView>(R.id.circularImageView)
        val displayname = view.findViewById<TextView>(R.id.displayname)
        val toolbar = view.findViewById<Toolbar>(R.id.phonetoolbar)
        sharedPreferences = requireContext().getSharedPreferences(requireContext().packageName+"user", MODE_PRIVATE)
        val username = sharedPreferences.getString("username","")
        val displaynametext = sharedPreferences.getString("displayname","")
        val imgurl = sharedPreferences.getString("imgurl","")
        Toast.makeText(requireContext(), auth.currentUser?.uid.toString() , Toast.LENGTH_SHORT).show()
        if(username=="" && displaynametext == "" && imgurl == ""){
            firestore.collection("users").document(auth.currentUser!!.uid).get().addOnSuccessListener {
                editor = sharedPreferences.edit()
                editor.putString("imgurl",it.getString("imageurl"))
                editor.putString("username",it.getString("username"))
                editor.putString("age",it.get("age").toString())
                editor.putString("birthdate",it.getString("birthdate"))
                editor.putString("displayname",it.getString("displayname"))
                editor.putString("email",it.getString("email"))
                editor.apply()
            }
        }
        storageReference = FirebaseStorage.getInstance().reference
        val vp = requireActivity().findViewById<View>(R.id.viewpager) as ViewPager2
        vp.isUserInputEnabled = false
        editprofile = view.findViewById(R.id.editprofile)
        editprofile.setOnClickListener {
            val intent = Intent(requireActivity(), EditProfile::class.java)
            intent.putExtra("groupinfo", "groupinfo")
            val bndlAnimation = ActivityOptions.makeCustomAnimation(
                requireContext(),
                R.anim.animenterslow,
                R.anim.animexitslow
            ).toBundle()
            startActivity(intent,bndlAnimation)
        }
        val switchaccount = view.findViewById<ImageView>(R.id.switchaccount)
        switchaccount.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences(requireContext().packageName+"addedaccounts", MODE_PRIVATE)
            var numberofaddedaccounts = 0
            if(sharedPreferences.getString("count","")!=""){
                numberofaddedaccounts = sharedPreferences.getString("count","")!!.toInt()}
            val modelList = ArrayList<SavedAccountModel>()
            if(numberofaddedaccounts == 0){
                val accountnumber = numberofaddedaccounts + 1
                val listOfKeys = listOf("imgurl","username","email","displayname","age","gender","password")
                val hashMap = SharedPreferenceHelper.getMultipleString(requireContext(),"user",listOfKeys)
                val newHash = hashMapOf<String,String>()
                listOfKeys.forEach { key->
                    newHash.put("key$accountnumber",hashMap[key].toString())
                }
                newHash["count"] = accountnumber.toString()
                SharedPreferenceHelper.putMultipleStrings(requireContext(),"addedaccounts",hashMap)
            }
            if(sharedPreferences.getString("count","")!=""){
                numberofaddedaccounts = sharedPreferences.getString("count","")!!.toInt()}
            for(j in 1..numberofaddedaccounts){
                val i = j-1
                val username = sharedPreferences.getString("username$j","").toString()
//                Toast.makeText(context, username, Toast.LENGTH_SHORT).show()
                val imageurl = sharedPreferences.getString("imgurl$j","").toString()
                val displayname = sharedPreferences.getString("displayname$j","").toString()
//                Toast.makeText(context, displayname, Toast.LENGTH_SHORT).show()
                val age = sharedPreferences.getString("age$j","").toString()
                val gender = sharedPreferences.getString("gender$j","").toString()
                val email = sharedPreferences.getString("email$j","").toString()
                val password = sharedPreferences.getString("password$j","").toString()
                val model = SavedAccountModel(username,imageurl,email,password,displayname,age,gender)
                modelList.add(index =i,model)
            }
            val adapter = ChangeAccountAdapter(requireContext(),modelList,this)
            BottomSheetDialogBuilder.createDialogWithImageAndRecyclerView(
                requireContext(),
                R.layout.changeaccountbottomsheet,
                R.id.addaccount,
                imageId = R.id.plus,
                R.id.changeaccountrecyclerview,
                adapter = adapter,
                layoutManager = LinearLayoutManager(requireContext()),
                onClickListener1 = {
                    BottomSheetDialogBuilder.dismissDialog()
                    showAddAccountDialog()
                },
                onClickListener2 = {
                    BottomSheetDialogBuilder.dismissDialog()
                    showAddAccountDialog()
                }
            )
        }

        val imageUrl = sharedPreferences.getString("imgurl", "")
        val settings = view.findViewById<ImageView>(R.id.settings)
        settings.setOnClickListener {
            startActivity(Intent(activity, Settings::class.java))
        }
        when {
            imageUrl.toString() == "male" -> {
                imageStream = this.resources.openRawResource(R.raw.gigachad)
                bitmap = BitmapFactory.decodeStream(imageStream)
                pfp.setImageBitmap(bitmap)
            }
            imageUrl.toString() == "female" -> {
                imageStream = this.resources.openRawResource(R.raw.doomergirl)
                bitmap = BitmapFactory.decodeStream(imageStream)
                pfp.setImageBitmap(bitmap)
            }
            else -> {
                Glide.with(requireContext()).load(imageUrl).circleCrop().into(pfp)
            }
        }
        pfp.setOnClickListener {
            showfirstDialog()
        }
        displayname.text = sharedPreferences.getString("displayname", "")
        toolbar.title = sharedPreferences.getString("username", "")
        mGetContent = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { result ->
            if (result != null) {
                dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.progressbardialog)
                val pleasewait = dialog.findViewById<TextView>(R.id.pleasewait)
                pleasewait.text = "Setting Profile Pic"
                dialog.setCancelable(false)
                dialog.show()
                imageurl = result.toString()
//                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,result);
//                createDirectoryAndSaveFile(bitmap,firebaseAuth.currentUser!!.uid)
                Glide.with(requireContext()).load(result).circleCrop().into(pfp)
                uploadToFirebase(result)
            }
//            next.visibility = View.VISIBLE
//            skip.visibility = View.INVISIBLE

        }
        getImageFromCamera = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.data != null) {
                val extras = result.data!!.extras
                if (extras != null) {
                    dialog = Dialog(requireContext())
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.progressbardialog)
                    val pleasewait = dialog.findViewById<TextView>(R.id.pleasewait)
                    pleasewait.text = "Setting Profile Pic"
                    dialog.setCancelable(false)
                    dialog.show()
//                    val extrauri = extras["data"] as Uri?
                    val imageBitmap = extras["data"] as Bitmap?
                    val filepath = context?.let { saveQualityImage(imageBitmap!!, it) }
                    Glide.with(requireContext()).load(filepath).circleCrop().into(pfp)
                    uploadToFirebase(filepath)
                    imageurl = filepath.toString()

                }
            }
        }
        return view
    }

    private fun uploadToFirebase(uri: Uri?) {
        if (uri != null) {
            val uId: String = firebaseAuth.currentUser!!.uid
            val fileRef: StorageReference =
                storageReference?.child("profileImages")!!.child("$uId.jpeg")
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
                editor = sharedPreferences.edit()
                editor.putString("imgurl", downloadUrl)
                editor.apply()
                FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.currentUser!!.uid).update(
                    "imageurl",downloadUrl
                )
                dialog.dismiss()
            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "Uploading Failed !!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
//        final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
    }
        private fun saveQualityImage(bm: Bitmap, context: Context): Uri? {
            val imagesFolder = File(context.cacheDir, "images")
            var uri: Uri? = null
            try {
                imagesFolder.mkdirs()
                val file = File(imagesFolder, System.currentTimeMillis().toString()+ ".jpeg")
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


    private fun showAddAccountDialog(){
        BottomSheetDialogBuilder.createDialog(
            requireContext(),
            R.layout.addaccountdialog,
            R.id.logintoexistingaccount,
            R.id.createnewaccount,
            {
                val intent = Intent(requireActivity(), Login::class.java)
                intent.putExtra("add","add")
                startActivity(intent)
            },{
                val intent = Intent(requireActivity(), Setup::class.java)
                intent.putExtra("add","add")
                startActivity(intent)
            }
        )
    }

//    private fun openCamera() {
//        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        val imagePath = createImage()
//        camera.putExtra(MediaStore.EXTRA_OUTPUT,imagePath)
//        getImageFromCamera!!.launch(camera)
//    }

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

        private val askgallerypermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                mGetContent!!.launch("image/*")
            } else {
                Toast.makeText(
                    context,
                    "We require permission to access files and media on your device in order to set your profile photo."
                            + " To setup your profile photo, go to Settings-> Apps -> Any1 -> Permissions -> grant Files and media permission",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun showfirstDialog() {
        BottomSheetDialogBuilder.createDialog(
            requireContext(),
            R.layout.pfpdialog69,
            R.id.newphoto,
            R.id.removephoto,
            {
                BottomSheetDialogBuilder.dismissDialog()
                showDialog()
            },
            {
                val sharedPreferences = requireContext().getSharedPreferences(requireContext().packageName+"user", MODE_PRIVATE)
                val firestore = FirebaseFirestore.getInstance()
                val auth = FirebaseAuth.getInstance()
                if(sharedPreferences.getString("gender","")=="male"){
                    sharedPreferences.edit().putString("imgurl","male").apply()
                    pfp.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.gigachad))
                    firestore.collection("users").document(auth.currentUser!!.uid).update("imageurl","male").addOnSuccessListener {
                        BottomSheetDialogBuilder.dismissDialog()
                    }
                }else{
                    sharedPreferences.edit().putString("imgurl","female").apply()
                    pfp.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.doomergirl))
                    firestore.collection("users").document(auth.currentUser!!.uid).update("imageurl","female").addOnCanceledListener {
                        BottomSheetDialogBuilder.dismissDialog()
                    }
                }
            }
        )
    }
        private fun showDialog() {
            BottomSheetDialogBuilder.createDialog(
                requireContext(),
                R.layout.pfpdialog,
                R.id.camera,
                R.id.materialTextView2,
                {
                    BottomSheetDialogBuilder.dismissDialog()
                    PermissionManager.askCameraPermission(requireActivity(),this)
                },
                {
                    BottomSheetDialogBuilder.dismissDialog()
                    PermissionManager.askGalleryPermission(requireActivity(),this)
                }
            )
        }



    override fun onAccountClick(savedAccountModel: SavedAccountModel) {
        val email = SharedPreferenceHelper.getString(requireContext(),"user","email")
        if(email!=savedAccountModel.email) {
            ProgressBarDialogBuilder.createDialog(requireContext(),"Switching Accounts")
            firebaseAuth.signInWithEmailAndPassword(
                savedAccountModel.email,
                savedAccountModel.password
            ).addOnSuccessListener {
                val hashMap = hashMapOf(
                    "username" to savedAccountModel.username,
                    "displayname" to savedAccountModel.displayname,
                    "imgurl" to savedAccountModel.imageurl,
                    "age" to savedAccountModel.age,
                    "gender" to savedAccountModel.gender,
                    "email" to savedAccountModel.email,
                    "password" to savedAccountModel.password
                )
                SharedPreferenceHelper.putMultipleStrings(requireContext(),"user",hashMap)
                val viewModel = ViewModelProvider(requireActivity()).get(GroupVM::class.java)
                viewModel.updateAuthId(firebaseAuth.currentUser!!.uid)
                ProgressBarDialogBuilder.dismissDialog()
                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.putExtra("switch","switch")
                requireActivity().finish()
                startActivity(intent)
            }
        }
    }

    override fun onPermissionGranted(permissionType: String) {
        if(permissionType == "gallery") mGetContent!!.launch("image/*")
        else{
            val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getImageFromCamera!!.launch(camera)
        }
    }
}