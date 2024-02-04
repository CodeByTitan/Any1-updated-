package com.example.any1.feature_chat.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.emoji.widget.EmojiTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.any1.R
import com.example.any1.feature_chat.domain.interfaces.BasicClickListener
import com.example.any1.feature_chat.domain.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ChatAdapter(val basicClickListener: BasicClickListener, val context: Context) :
    RecyclerView.Adapter<ChatAdapter.BaseViewHolder>() {
    val MESSAGE_RIGHT = 0 // FOR USER LAYOUT
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    override fun getItemViewType(position: Int): Int {
        val firebaseAuth = FirebaseAuth.getInstance()
        val chatModel: ChatModel = chatModelList[position]
        return if (chatModel.sender == firebaseAuth.currentUser!!.uid) {
            MESSAGE_RIGHT
        } else {
            MESSAGE_LEFT
        }

    }

    private fun isEmoji(message: String): Boolean {
        return message.matches(
            Regex(
                "(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|" +
                        "[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|" +
                        "[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|" +
                        "[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|" +
                        "[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|" +
                        "[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|" +
                        "[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|" +
                        "[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|" +
                        "[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|" +
                        "[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|" +
                        "[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)+"
            )
        )
    }

    val MESSAGE_LEFT = 1 // FOR FRIEND LAYOUT
    private val chatModelList = ArrayList<ChatModel>()

    fun setChatModelList(messageModelList: ArrayList<ChatModel>?) {
        if (messageModelList != null) {
            chatModelList.addAll(messageModelList)
        }
    }

    fun clearChatModelList() {
        chatModelList.clear()
    }

    inner class ChatLeftHolder(itemView: View) : BaseViewHolder(itemView) {
        val textView: EmojiTextView = itemView.findViewById(R.id.showemojimessage)
        val imageView: ImageView = itemView.findViewById(R.id.senderpfp)
        val relativeLayout: RelativeLayout = itemView.findViewById(R.id.relativeleft)

        init {
            imageView.setOnClickListener {
                basicClickListener.onClick()
            }
        }

        override fun bind(item: String) {
            textView.text = item
            assignMessageBackground(item, textView, adapterPosition, relativeLayout)
        }

        override fun bindUri(uri: String) {
            if (uri != "male") {
                if (uri != "female") {
                    Glide.with(context).load(uri).circleCrop().into(imageView)
                } else {
                    imageView.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.doomergirl
                        )
                    )
                }
            } else {
                imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.gigachad
                    )
                )
            }
        }
    }

    private fun assignMessageBackground(
        item: String,
        emojiTextView: EmojiTextView,
        position: Int,
        relativeLayout: RelativeLayout
    ) {
        if (isEmoji(item)) {
            if (item.length > 20) {
                emojiTextView.textSize = 15f
                assignBackgroundForNormalMessage(emojiTextView, position, relativeLayout)
            } else {
                emojiTextView.textSize = 30f
                emojiTextView.background = null
                emojiTextView.setPadding(0, 10, 0, 10)
            }
        } else {
            assignBackgroundForNormalMessage(emojiTextView, position, relativeLayout)
        }
    }

    private fun assignBackgroundForNormalMessage(
        emojiTextView: EmojiTextView,
        position: Int,
        relativeLayout: RelativeLayout
    ) {
        emojiTextView.textSize = 15f
        if (chatModelList[position].sender == auth.currentUser!!.uid) {
            if (position != 0) {
                emojiTextView.setPadding(40, 30, 30, 30)
                if (position == chatModelList.size - 1) {
                    emojiTextView.background =
                        AppCompatResources.getDrawable(context, R.drawable.backgroundrightfirstmsg)
                } else {
                    if ((chatModelList[position - 1].sender == auth.currentUser!!.uid) && (chatModelList[position + 1].sender == auth.currentUser!!.uid)) {
                        emojiTextView.background =
                            AppCompatResources.getDrawable(context, R.drawable.backgroundright)
                    } else if (chatModelList[position + 1].sender != auth.currentUser!!.uid && chatModelList[position - 1].sender == auth.currentUser!!.uid) {
                        emojiTextView.background = AppCompatResources.getDrawable(
                            context,
                            R.drawable.backgroundrightfirstmsg
                        )
                    } else if (chatModelList[position - 1].sender != auth.currentUser!!.uid && chatModelList[position + 1].sender == auth.currentUser!!.uid) {
                        emojiTextView.background = AppCompatResources.getDrawable(
                            context,
                            R.drawable.backgroundrightlastmessage
                        )
                    } else if ((chatModelList[position - 1].sender != auth.currentUser!!.uid) && (chatModelList[position + 1].sender != auth.currentUser!!.uid)) {
                        emojiTextView.background =
                            AppCompatResources.getDrawable(context, R.drawable.backgroundright)
                    }
                }
            } else {
                val linearParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                )
                linearParams.setMargins(0, 2, 15, 10)
                relativeLayout.layoutParams = linearParams
                relativeLayout.requestLayout()
                emojiTextView.setPadding(40, 30, 30, 30)
                emojiTextView.background =
                    AppCompatResources.getDrawable(context, R.drawable.backgroundrightlastmessage)
            }
        } else {
            if (position != 0) {
                emojiTextView.setPadding(30, 30, 40, 30)
                if (position == chatModelList.size - 1) {
                    emojiTextView.background = AppCompatResources.getDrawable(
                        context,
                        R.drawable.chatleftfirstmessage
                    )
                } else {
                    if ((chatModelList[position + 1].sender == chatModelList[position].sender) && (chatModelList[position - 1].sender == chatModelList[position].sender)) {
                        emojiTextView.background =
                            AppCompatResources.getDrawable(context, R.drawable.backgroundleft)
                    } else if (chatModelList[position + 1].sender != chatModelList[position].sender && chatModelList[position - 1].sender == chatModelList[position].sender) {
                        emojiTextView.background = AppCompatResources.getDrawable(
                            context,
                            R.drawable.chatleftfirstmessage
                        )
                    } else if (chatModelList[position - 1].sender != chatModelList[position].sender && chatModelList[position + 1].sender == chatModelList[position].sender) {
                        emojiTextView.background = AppCompatResources.getDrawable(
                            context,
                            R.drawable.chatleftlastmessage
                        )
                    } else if ((chatModelList[position - 1].sender != chatModelList[position].sender) && (chatModelList[position + 1].sender != chatModelList[position].sender)) {
                        emojiTextView.background =
                            AppCompatResources.getDrawable(context, R.drawable.backgroundleft)
                    }
                }
            } else {
                emojiTextView.setPadding(30, 30, 40, 30)
                val linearParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                )
                linearParams.setMargins(15, 2, 0, 10)
                relativeLayout.layoutParams = linearParams
                relativeLayout.requestLayout()
                emojiTextView.background = AppCompatResources.getDrawable(
                    context,
                    R.drawable.chatleftlastmessage
                )
            }
        }
    }

    inner class ChatRightHolder(itemView: View) : BaseViewHolder(itemView) {
        val emojiTextView: EmojiTextView = itemView.findViewById(R.id.showemojimessage)
        val relativeLayout: RelativeLayout = itemView.findViewById(R.id.relativeright)
        override fun bind(item: String) {
            emojiTextView.text = item
            assignMessageBackground(item, emojiTextView, adapterPosition, relativeLayout)
        }

        override fun bindUri(uri: String) {
            return
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        var holder: BaseViewHolder? = null
        return if (viewType == MESSAGE_RIGHT) {
            holder = ChatRightHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_right, parent, false)
            )
            holder
        } else {
            holder = ChatLeftHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_left, parent, false)
            )
            holder
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(chatModelList[position].item)
        if (getItemViewType(position) == MESSAGE_LEFT) {
            holder.bindUri(chatModelList[position].senderpfpuri)
        }

    }

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: String)
        abstract fun bindUri(uri: String)
    }

    override fun getItemCount(): Int {
        return chatModelList.size
    }
}