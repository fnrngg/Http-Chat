package com.example.httpchat.ui.register

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.httpchat.databinding.ActivityRegisterBinding
import com.example.httpchat.models.User
import com.example.httpchat.ui.messages.MessagesActivity
import java.io.ByteArrayOutputStream
import java.io.InputStream


class RegisterActivity : AppCompatActivity(), RegisterContract.View {
    companion object {
        private const val PICK_IMAGE = 0

        fun start(context: Context) {
            val intent = Intent(context, RegisterActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityRegisterBinding
    private var image: String? = null
    private lateinit var presenter: RegisterPresenterImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = RegisterPresenterImpl(this)
        binding.startButton.setOnClickListener {
            if (binding.doingTextField.text.isNullOrEmpty() or binding.nickNameTextField.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                presenter.saveUser(
                    User(
                        binding.nickNameTextField.text.toString(),
                        binding.doingTextField.text.toString(),
                        image
                    )
                )
            }
        }

        binding.userImage.setOnClickListener {
            pickImage()
        }
    }


    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            binding.userImage.setImageURI(data?.data)
            val imageStream: InputStream? = contentResolver.openInputStream(data?.data!!)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            image = toBase64(selectedImage)

        }
    }

    private fun toBase64(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        return Base64.encodeToString(b, Base64.NO_WRAP)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dispose()
    }

    override fun startActivity() {
//        Log.d("ragaca","ragaca")
        MessagesActivity.start(this)
    }

}