package com.wzx.appshortcuts

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.NonNull
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View

/**
 * 通过Menu触发添加动态快捷方式
 *
 * 关于shortcut（快捷方式）：
 * 1.只能显示4个快捷方式，并且只能添加5个，继续添加会替换最后一个
 * 2.静态快捷方式不能删除
 * 3.移除快捷方式，不影响桌面该快捷方式正常使用
 * 4.rank越大离launcher越远
 * 5.disableShortcuts后，快捷图标会变成灰色
 */
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShortcutAdapter

    lateinit var shoetcutManager: ShortcutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        log("onCreate")

        initViews()
        initShortcut()
    }

    override fun onResume() {
        super.onResume()

        log("onResume")
    }

    override fun onStart() {
        super.onStart()

        log("onStart")

        adapter.newData(queryShortcuts())
    }

    override fun onStop() {
        log("onStop")
        super.onStop()
    }

    override fun onDestroy() {
        log("onDestroy")
        super.onDestroy()
    }

    /**
     * 创建快捷方式
     *
     */
    fun createShortcut(@NonNull shortcutId: String,
                       @NonNull shortLabel: String,
                       @NonNull longLabel: String,
                       @NonNull disableMessage: String,
                       @NonNull intent: Intent,
                       @NonNull rank: Int) = ShortcutInfo.Builder(this, shortcutId)
            .setShortLabel(shortLabel)
            .setLongLabel(longLabel)
            .setDisabledMessage(disableMessage)
            .setRank(rank)
            .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
            .setIntent(intent)
            .build()

    /**
     * 添加快捷方式
     */
    fun addShortcut(@NonNull shortcutId: String,
                    @NonNull shortLabel: String,
                    @NonNull longLabel: String,
                    @NonNull disableMessage: String,
                    @NonNull intent: Intent,
                    @NonNull rank: Int) {
        shoetcutManager.addDynamicShortcuts(arrayListOf(createShortcut(shortcutId, shortLabel, longLabel, disableMessage, intent, rank)))
        adapter.newData(queryShortcuts())
    }

    /**
     * dynamicShortcuts中不包含禁用快捷方式
     */
    fun queryShortcuts(): ArrayList<ShortcutInfo> {
        var list = arrayListOf<ShortcutInfo>()
        list.addAll(shoetcutManager.dynamicShortcuts)
        list.addAll(shoetcutManager.manifestShortcuts)
        return list
    }

    /**
     * 更新快捷方式
     */
    fun updateShortcut(@NonNull shortcuts: List<ShortcutInfo>) {
        shoetcutManager.updateShortcuts(shortcuts)
    }

    /**
     * 禁用快捷方式
     * @param shortcutIds
     */
    fun disableShortcut(@NonNull shortcutIds: List<String>) {
        shoetcutManager.disableShortcuts(shortcutIds)
    }

    /**
     * 静态快捷方式不可删除
     * @param shortcutIds
     */
    fun removeShortcuts(@NonNull shortcutIds: List<String>) {
        shoetcutManager?.removeDynamicShortcuts(shortcutIds)
        adapter.newData(queryShortcuts())
    }

    /**
     * 静态快捷方式不会删除
     */
    fun removeAllShortcuts() {
        shoetcutManager?.removeAllDynamicShortcuts()
        adapter.newData(queryShortcuts())
    }

    fun initViews() {
        supportActionBar?.title = "快捷方式"

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        adapter = ShortcutAdapter(arrayListOf(), object : ShortcutAdapter.OnShortcutCliclListener {
            override fun onItemLongClick(position: Int, shortcutInfo: ShortcutInfo) {
                if (shortcutInfo.isDynamic) {
                    UpdateDialog.newInstance(shortcutInfo).addOnChangedListener(object : UpdateDialog.OnChangedListener {
                        override fun onDisable(id: String) {
                            disableShortcut(listOf(id))
                            adapter.newData(queryShortcuts())
                        }

                        override fun onChange(shortcutInfo: ShortcutInfo) {
                            updateShortcut(listOf(shortcutInfo))
                            adapter.newData(queryShortcuts())
                        }
                    }).show(supportFragmentManager, UpdateDialog.TAG)
                } else {
                    showSnackBar(recyclerView, "静态快捷方式不可修改！")
                }
            }

            override fun onItemClick(position: Int, shortcutInfo: ShortcutInfo) {

            }

            override fun onDeleteClick(position: Int, shortcutInfo: ShortcutInfo) {
                if (shortcutInfo.isDynamic) {
                    removeShortcuts(listOf(shortcutInfo.id))
                } else {
                    showSnackBar(recyclerView, "静态快捷方式不可删除")
                }
            }
        })

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun initShortcut() {
        shoetcutManager = getSystemService(ShortcutManager::class.java)
    }

    fun showSnackBar(@NonNull view: View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }

    /**
     * 关于menu中的icon从Android4.0开始默认不显示
     * @param menu
     * @param visible
     */
    fun showIcon(menu: Menu?, visible: Boolean) {
        //反射拿到menu的setOptionalIconsVisible()，然后设置显示icon
        val method = menu?.javaClass!!.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.java)
        method.isAccessible = true
        method.invoke(menu, visible)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        showIcon(menu, true)
        menuInflater.inflate(R.menu.actionbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.option_call -> {
                //TODO 无法使用
                addShortcut("call",
                        "拨号：13700000000",
                        "拨号：13700000000",
                        "不可用",
                        Intent(Intent.ACTION_CALL, Uri.parse("tel://13700000000")),
                        1)
                return true
            }
            R.id.option_dial -> {
                addShortcut("dial",
                        "拨号面板",
                        "预拨：13700000000",
                        "不可用",
                        Intent(Intent.ACTION_DIAL, Uri.parse("tel://13700000000")),
                        1)
                return true
            }
            R.id.option_message -> {
                addShortcut("message",
                        "发送短信",
                        "发送短信",
                        "不可用",
                        Intent(Intent.ACTION_SENDTO, Uri.parse("smsto://13700000000")).putExtra("sms_body", "发送短信..."),
                        1)
                return true
            }
            R.id.option_camera -> {
                //TODO 无法使用
                addShortcut("camera",
                        "相机",
                        "相机",
                        "不可用",
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE, Uri.EMPTY),
                        1)
                return true
            }
            R.id.option_share -> {
                addShortcut("camera",
                        "分享",
                        "分享",
                        "不可用",
                        Intent(Intent.ACTION_SEND, Uri.EMPTY).setType("text/plain").putExtra(Intent.EXTRA_TEXT, "这是一段分享的文字"),
                        1)
                return true
            }
            R.id.option_delete -> {
                removeAllShortcuts()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}
