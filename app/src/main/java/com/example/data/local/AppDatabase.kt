package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.NoteDao
import com.example.data.local.dao.PlannerEventDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.entities.NoteEntity
import com.example.data.local.entities.PlannerEventEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        TaskEntity::class,
        NoteEntity::class,
        TransactionEntity::class,
        PlannerEventEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao
    abstract fun transactionDao(): TransactionDao
    abstract fun plannerEventDao(): PlannerEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "personal_organizer.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Seed initial helpful starter data
                CoroutineScope(Dispatchers.IO).launch {
                    INSTANCE?.let { database ->
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())

                // 1. Starter Tasks
                val taskDao = database.taskDao()
                taskDao.insertTask(
                    TaskEntity(
                        title = "مراجعة تقرير خطة العمل الأسبوعية",
                        description = "إعداد الملخص للمشاريع الحالية وتحديد الأولويات",
                        priority = "HIGH",
                        category = "عمل",
                        dueDate = todayStr,
                        dueTime = "10:00",
                        isCompleted = false
                    )
                )
                taskDao.insertTask(
                    TaskEntity(
                        title = "شراء مستلزمات المنزل والأغذية",
                        description = "خضروات، فواكه، مستلزمات المطبخ، ومشروبات",
                        priority = "MEDIUM",
                        category = "تسوق",
                        dueDate = todayStr,
                        dueTime = "17:30",
                        isCompleted = false
                    )
                )
                taskDao.insertTask(
                    TaskEntity(
                        title = "ممارسة رياضة المشي لمدة 30 دقيقة",
                        description = "الحفاظ على النشاط البدني اليومي وشرب الماء الكافي",
                        priority = "LOW",
                        category = "صحة",
                        dueDate = todayStr,
                        dueTime = "19:00",
                        isCompleted = true
                    )
                )
                taskDao.insertTask(
                    TaskEntity(
                        title = "قراءة 20 صفحة من كتاب تطوير الذات",
                        description = "كتاب العادات الذرية - الفصل الرابع",
                        priority = "MEDIUM",
                        category = "دراسة",
                        dueDate = todayStr,
                        dueTime = "21:00",
                        isCompleted = false
                    )
                )

                // 2. Starter Notes
                val noteDao = database.noteDao()
                noteDao.insertNote(
                    NoteEntity(
                        title = "أهداف الشهر الجديد",
                        content = "1. تحسين الروتين الصباحي والاستيقاظ باكراً.\n2. إدخار 25% من الدخل الشهري في حساب الطوارئ.\n3. إنهاء دورة إدارة المشاريع الاحترافية.\n4. قضاء وقت نوعي مع العائلة في عطلة نهاية الأسبوع.",
                        category = "مهم",
                        colorHex = "#FEF3C7", // warm amber
                        isPinned = true
                    )
                )
                noteDao.insertNote(
                    NoteEntity(
                        title = "أفكار لمشروع تطبيقي مبتكر",
                        content = "تصميم تطبيق يربط بين تنظيم الوقت والمحفظة المالية بأسلوب بسيط وسريع للمستخدم العربي بدون تعقيد.",
                        category = "أفكار",
                        colorHex = "#DCFCE7", // soft emerald
                        isPinned = true
                    )
                )
                noteDao.insertNote(
                    NoteEntity(
                        title = "قائمة مشتريات دورية",
                        content = "دفاتر ملاحظات، بطاريات شحن، كتاب جديد، قهوة مختصة.",
                        category = "شخصي",
                        colorHex = "#E0F2FE", // soft sky
                        isPinned = false
                    )
                )

                // 3. Starter Transactions
                val transactionDao = database.transactionDao()
                transactionDao.insertTransaction(
                    TransactionEntity(
                        title = "راتب الشهر الأساسي",
                        amount = 6500.0,
                        type = "INCOME",
                        category = "راتب",
                        date = todayStr,
                        notes = "التحويل البنكي الشهري"
                    )
                )
                transactionDao.insertTransaction(
                    TransactionEntity(
                        title = "مشروع برمجي مستقل (Freelance)",
                        amount = 1200.0,
                        type = "INCOME",
                        category = "عمل حر",
                        date = todayStr,
                        notes = "دفعة تسليم المشروع"
                    )
                )
                transactionDao.insertTransaction(
                    TransactionEntity(
                        title = "مشتريات السوبرماركت",
                        amount = 340.0,
                        type = "EXPENSE",
                        category = "طعام",
                        date = todayStr,
                        notes = "مؤونة الأسبوع"
                    )
                )
                transactionDao.insertTransaction(
                    TransactionEntity(
                        title = "فاتورة الإنترنت المنزلي السريع",
                        amount = 180.0,
                        type = "EXPENSE",
                        category = "فواتير",
                        date = todayStr,
                        notes = "اشتراك الألياف البصرية"
                    )
                )
                transactionDao.insertTransaction(
                    TransactionEntity(
                        title = "وقود السيارة والتنقل",
                        amount = 120.0,
                        type = "EXPENSE",
                        category = "مواصلات",
                        date = todayStr,
                        notes = "تعبئة خزان الوقود"
                    )
                )

                // 4. Starter Planner Events
                val plannerDao = database.plannerEventDao()
                plannerDao.insertEvent(
                    PlannerEventEntity(
                        title = "اجتماع فريق العمل لمناقشة الأهداف",
                        date = todayStr,
                        startTime = "09:30",
                        endTime = "10:30",
                        tag = "اجتماع",
                        location = "غرفة الاجتماعات الافتراضية",
                        notes = "إحضار ملخص الأداء للشهر الماضي",
                        isDone = false
                    )
                )
                plannerDao.insertEvent(
                    PlannerEventEntity(
                        title = "موعد فحص الأسنان الدوري",
                        date = todayStr,
                        startTime = "15:00",
                        endTime = "15:45",
                        tag = "موعد",
                        location = "عيادة الأمل الطبية",
                        notes = "تأكيد الحجز مسبقاً",
                        isDone = false
                    )
                )
                plannerDao.insertEvent(
                    PlannerEventEntity(
                        title = "جلسة ترفيهية مع الأصدقاء",
                        date = todayStr,
                        startTime = "20:00",
                        endTime = "22:00",
                        tag = "شخصي",
                        location = "المقهى الثقافي",
                        notes = "مناقشة أفكار الكتاب الجديد",
                        isDone = false
                    )
                )
            }
        }
    }
}
