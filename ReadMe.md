### AppShortcuts（快捷方式）

        需要在Android 7.1及以上版本才能正常使用
        
        快捷方式注册可分类两种：
        1.静态注册
            实现步骤：
                a.在res/xml中新建一个xxx.xml
                    <?xml version="1.0" encoding="utf-8"?>
                    <shortcuts xmlns:android="http://schemas.android.com/apk/res/android">
                    
                        <!--
                            enabled：是否启用快捷方式（可选）
                            icon：图标（可选）
                            shortcutDisabledMessage：禁用快捷方式后，当触发该快捷方式会以toast提示这个内容（可选）
                            shortcutId：快捷方式唯一编号（必填）
                            shortcutLongLabel：快捷方式简述（可选）
                            shortcutShortLabel：快捷方式详细描述（必填）
                            intent：快捷方式Intent（必填）
                        -->
                        <shortcut
                            android:enabled="true"
                            android:icon="@mipmap/ic_launcher"
                            android:shortcutDisabledMessage="@string/open_app_disabled"
                            android:shortcutId="open_app"
                            android:shortcutLongLabel="@string/open_app_long"
                            android:shortcutShortLabel="@string/open_app_short">
                    
                            <intent
                                android:action="android.intent.action.VIEW"
                                android:targetClass="com.wzx.appshortcuts.MainActivity"
                                android:targetPackage="com.wzx.appshortcuts" />
                    
                            <categories android:name="android.shortcut.conversation" />
                    
                        </shortcut>
                    
                    </shortcuts>
                 
                b.在Manifest.xml中注册
                    <activity android:name=".MainActivity">
                        <intent-filter>
                            <action android:name="android.intent.action.MAIN" />
                                <category android:name="android.intent.category.LAUNCHER" />
                        </intent-filter>
                        <meta-data
                            android:name="android.app.shortcuts"
                            android:resource="@xml/shortcuts" />
                    </activity>
                    
        2.动态注册
            通过ShortcutManager管理ShortcutInfo
            实现步骤：
            a.创建ShortcutInfo
                ShortcutInfo.Builder(context, shortcutId)
                            .setShortLabel(shortLabel)
                            .setLongLabel(longLabel)
                            .setDisabledMessage(disableMessage)
                            .setRank(rank)
                            .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))
                            .setIntent(intent)
                            .build()
            
            b.ShortcutManager添加addDynamicShortcuts(ShortcutInfo)
            
        
        ShortcutManager其他方法：
            updateShortcuts(List<shortcutInfo>)：更新快捷方式
            disableShortcuts(List<String>)：禁用快捷方式
            removeDynamicShortcuts(List<String>)：移除快捷方式
            
        注意事项：
        * 只能显示4个快捷方式，但是能添加5个快捷方式，当超过5个时，替换最后一个
        * 静态快捷方式不能更新、删除、禁用
        * 移除快捷方式，不影响桌面该快捷方式正常使用
        * rank越大离launcher越远
        * disableShortcuts后，快捷图标会变成灰色