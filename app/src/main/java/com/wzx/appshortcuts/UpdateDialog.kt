package com.wzx.appshortcuts

import android.content.pm.ShortcutInfo
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText


/**
 * 描述：
 *
 * 创建人： Administrator
 * 创建时间： 2018/9/27
 * 更新时间：
 * 更新内容：
 */
class UpdateDialog : DialogFragment(), View.OnClickListener {


    lateinit var idET: EditText
    lateinit var shortLabelET: EditText
    lateinit var longLabelET: EditText
    lateinit var messageET: EditText
    lateinit var rankET: EditText

    lateinit var cancelBTN: Button
    lateinit var disableBTN: Button
    lateinit var updateBTN: Button

    private var listener: OnChangedListener? = null

    companion object {

        const val TAG = "UpdateDialog"
        const val EXTRAS_SHORTCUTINFO = "shortcutinfo"

        fun newInstance(shortcutInfo: ShortcutInfo): UpdateDialog {
            val arguments = Bundle()
            arguments.putParcelable(EXTRAS_SHORTCUTINFO, shortcutInfo)

            val dialog = UpdateDialog()
            dialog.arguments = arguments
            return dialog
        }
    }

    interface OnChangedListener {
        fun onChange(shortcutInfo: ShortcutInfo)
        fun onDisable(id: String)
    }

    fun addOnChangedListener(listener: OnChangedListener): UpdateDialog {
        this.listener = listener
        return this
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.dialog_update, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idET = view?.findViewById(R.id.et_id)!!
        shortLabelET = view?.findViewById(R.id.et_shortLabel)!!
        longLabelET = view?.findViewById(R.id.et_longLabel)!!
        messageET = view?.findViewById(R.id.et_disablemessage)!!
        rankET = view?.findViewById(R.id.et_rank)!!

        cancelBTN = view?.findViewById(R.id.btn_cancel)!!
        disableBTN = view?.findViewById(R.id.btn_disable)!!
        updateBTN = view?.findViewById(R.id.btn_update)!!

        cancelBTN.setOnClickListener(this)
        disableBTN.setOnClickListener(this)
        updateBTN.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        showShortcutInfo(arguments.getParcelable<ShortcutInfo>(EXTRAS_SHORTCUTINFO))
    }

    /**
     * @param shortcutInfo
     */
    fun showShortcutInfo(shortcutInfo: ShortcutInfo?) {
        idET.setText(shortcutInfo?.id)
        shortLabelET.setText(shortcutInfo?.shortLabel)
        longLabelET.setText(shortcutInfo?.longLabel)
        messageET.setText(shortcutInfo?.disabledMessage)
        rankET.setText(shortcutInfo?.rank.toString())
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_update -> {
                listener?.onChange(createShortcut(idET.text.toString(),
                        shortLabelET.text.toString(),
                        longLabelET.text.toString(),
                        messageET.text.toString(),
                        rankET.text.toString().toInt()))
                dismiss()
            }
            R.id.btn_disable -> {
                listener?.onDisable(idET.text.toString())
                dismiss()
            }
            else -> {
                dismiss()
            }
        }
    }

    fun createShortcut(@NonNull shortcutId: String,
                       @NonNull shortLabel: String,
                       @NonNull longLabel: String,
                       @NonNull disableMessage: String,
                       @NonNull rank: Int) = ShortcutInfo.Builder(context, shortcutId)
            .setShortLabel(shortLabel)
            .setLongLabel(longLabel)
            .setDisabledMessage(disableMessage)
            .setRank(rank)
            .build()
}