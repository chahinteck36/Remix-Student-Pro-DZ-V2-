package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.StudentProViewModel

enum class ThesisSubSection(val titleAr: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    STRUCTURE("هيكل المذكرة الشامل", Icons.Outlined.AccountTree),
    DOCUMENTS("مسودات وملفات (Word/PDF/XML)", Icons.Outlined.FolderSpecial),
    SURVEY("نماذج الاستبيان والتحكيم", Icons.Outlined.Quiz),
    STATS_ANALYSIS("تحليل SPSS والجداول", Icons.Outlined.BarChart),
    DEFENSE_SLIDES("سلايدات المناقشة PPT", Icons.Outlined.CoPresent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThesisPfeScreen(
    viewModel: StudentProViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentSection by remember { mutableStateOf(ThesisSubSection.STRUCTURE) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Section Selector Tabs
        ScrollableTabRow(
            selectedTabIndex = currentSection.ordinal,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            ThesisSubSection.values().forEach { sec ->
                Tab(
                    selected = currentSection == sec,
                    onClick = { currentSection = sec },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(sec.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(sec.titleAr, fontWeight = if (currentSection == sec) FontWeight.Bold else FontWeight.Normal)
                        }
                    },
                    modifier = Modifier.testTag("thesis_tab_${sec.name}")
                )
            }
        }

        if (currentSection == ThesisSubSection.DOCUMENTS) {
            AcademicDocumentsView(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // NotebookLM Quick Access Banner for Thesis
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1A73E8).copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    Icons.Default.CollectionsBookmark,
                                    contentDescription = null,
                                    tint = Color(0xFF1A73E8),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "مفكرة Google NotebookLM لمذكرتك",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF1A73E8)
                                    )
                                    Text(
                                        "اربط مقالاتك ودراساتك السابقة لصياغة الإشكالية وتوليد نقاش صوتي.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            TextButton(
                                onClick = { viewModel.setTab(com.example.ui.viewmodel.MainAppTab.NOTEBOOK_LM) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("فتح المفكرة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A73E8))
                            }
                        }
                    }
                }
                when (currentSection) {
                    ThesisSubSection.STRUCTURE -> {
                        item {
                            ThesisStructureCard(onCopy = { title, text -> viewModel.copyToClipboard(context, text, title) })
                        }
                    }
                    ThesisSubSection.DOCUMENTS -> { /* handled above */ }
                    ThesisSubSection.SURVEY -> {
                        item {
                            SurveyBuilderCard(onCopy = { title, text -> viewModel.copyToClipboard(context, text, title) })
                        }
                    }
                    ThesisSubSection.STATS_ANALYSIS -> {
                        item {
                            StatisticalAnalysisCard(onCopy = { title, text -> viewModel.copyToClipboard(context, text, title) })
                        }
                    }
                    ThesisSubSection.DEFENSE_SLIDES -> {
                        item {
                            DefensePowerPointCard(onCopy = { title, text -> viewModel.copyToClipboard(context, text, title) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThesisStructureCard(onCopy: (String, String) -> Unit) {
    val structureSnippet = """
🎓 الهيكل القياسي الكامل لمذكرة التخرج (ماستر / ليسانس / مهندس):

1. الصفحات التمهيدية (ترقم بالحروف أ، ب، ج...):
   - صفحة الغلاف الرسمية (Page de garde)
   - ورقة التثبيت / التفويض
   - صفحة الشكر والتقدير (Remerciements)
   - صفحة الإهداء (Dédicace)
   - الملخص بالعربية + الكلمات المفتاحية (Résumé en Arabe)
   - الملخص بالفرنسية + الكلمات المفتاحية (Résumé en Français)
   - الملخص بالإنجليزية (Abstract in English)
   - فهرس المحتويات العام (Table des matières)
   - قائمة الجداول (Liste des tableaux)
   - قائمة الأشكال والرسوم البيانية (Liste des figures)
   - قائمة الاختصارات والرموز (Liste des abréviations)

2. المتن الرئيسي للبحث (يبدأ الترقيم بالرقم 1 من المقدمة):
   - المقدمة العامة (Introduction Générale)
   - الفصل الأول: الإطار المفاهيمي والنظري للمتغير المستقل.
   - الفصل الثاني: الإطار النظري للمتغير التابع والدراسات السابقة.
   - الفصل الثالث: الإطار المنهجي والدراسة التطبيقية في المؤسسة.
   - الفصل الرابع: عرض وتحليل ومناقشة النتائج واختبار الفرضيات.
   - الخاتمة العامة والتوصيات (Conclusion Générale et Recommandations).

3. الصفحات الختامية:
   - قائمة المراجع والمصادر (مرتبة هجائياً: مراجع عربية، ثم مراجع أجنبية).
   - الملاحق (Annexes): نموذج الاستبيان النهائي، الوثائق الإدارية للمؤسسة، مخرجات SPSS.
   - الفهرس التفصيلي.
    """.trimIndent()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "خريطة وهيكل مذكرة التخرج المعتمدة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                FilledTonalButton(
                    onClick = { onCopy("هيكل مذكرة التخرج", structureSnippet) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("copy_thesis_structure")
                ) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("نسخ الهيكل", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                structureSnippet,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SurveyBuilderCard(onCopy: (String, String) -> Unit) {
    val likertTemplate = """
📋 نموذج استبيان علمي جاهز وفق مقياس ليكرت الخماسي (Échelle de Likert 5 points):

عنوان الاستبيان: دور [المتغير المستقل] في تحسين [المتغير التابع] في مؤسسة [اسم المؤسسة]

عزيزي المشارك / عزيزتي المشاركة:
تحية طيبة وبعد، نضع بين أيديكم هذا الاستبيان العلمي الموجه لجمع البيانات اللازمة لإعداد مذكرة تخرج لنيل شهادة الماستر في تخصص [...]. نرجو منكم التكرم بالإجابة الدقيقة على العبارات، ونؤكد لكم أن إجاباتكم ستعامل بسرية تامة وتستخدم لأغراض البحث العلمي فقط.

أولاً: البيانات الديموغرافية والشخصية:
1. الجنس: [ ] ذكر     [ ] أنثى
2. الفئة العمرية: [ ] أقل من 30 سنة   [ ] من 30 إلى 45 سنة   [ ] أكثر من 45 سنة
3. المستوى التعليمي: [ ] ثانوي فأقل   [ ] ليسانس / بكالوريا+3   [ ] ماستر / ماجستير   [ ] دكتوراه
4. سنوات الخبرة المهنية: [ ] أقل من 5 سنوات   [ ] 5-10 سنوات   [ ] أكثر من 10 سنوات
5. المستوى الإداري / الوظيفة: [ ] إدارة عليا   [ ] إدارة وسطى   [ ] موظف تنفيذي

ثانياً: محاور الاستبيان (مقياس ليكرت 5 درجات):
(1: غير موافق بشدة | 2: غير موافق | 3: محايد | 4: موافق | 5: موافق بشدة)

المحور الأول: واقع تطبيق [المتغير المستقل X]:
- العبارة 1: توفر المؤسسة البنية التحتية التكنولوجية اللازمة لأداء المهام.
- العبارة 2: تحرص الإدارة على تدريب الموظفين بصفة دورية.
- العبارة 3: تتبنى الإدارة استراتيجيات واضحة ومكتوبة للتحول الرقمي.

المحور الثاني: مستوى [المتغير التابع Y]:
- العبارة 4: يساعد النظام الحالي على إنجاز المعاملات بدقة وسرعة عالية.
- العبارة 5: يساهم النظام في تقليل الأخطاء الإدارية والمالية.
- العبارة 6: يحقق النظام مستوى رضا مرتفع لدى الزبائن والمتعاملين.

ثالثاً: شروط التحكيم العلمي للاستبيان:
1. عرض الاستبيان في نسخته الأولية على لجنة تحكيم تتكون من (3 إلى 5) أساتذة خبراء.
2. حساب معامل الاتساق الداخلي (Alpha de Cronbach) ويشترط أن يكون (α ≥ 0.70) لضمان ثبات الاستبيان.
    """.trimIndent()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "نموذج الاستبيان ومقياس ليكرت والتحكيم",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                FilledTonalButton(
                    onClick = { onCopy("نموذج الاستبيان", likertTemplate) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("copy_survey_template")
                ) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("نسخ الاستبيان", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                likertTemplate,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun StatisticalAnalysisCard(onCopy: (String, String) -> Unit) {
    val statsSnippet = """
📊 دليل التحليل الإحصائي وقوالب الجداول (SPSS / Excel):

1. خطوات تفريغ الاستبيان في SPSS:
   - ترميز البيانات (Variable View): إعطاء قيم للأوزان (1=غير موافق بشدة ... 5=موافق بشدة).
   - إدخال الإجابات (Data View).

2. الاختبارات الإحصائية الإلزامية:
   - اختبار ألفا كرونباخ (Fiabilité - Alpha de Cronbach): للتحقق من ثبات أداة الدراسة.
   - اختبار التوزيع الطبيعي (Kolmogorov-Smirnov / Shapiro-Wilk): لتحديد استخدام الاختبارات المعلمية (Parametric) أو غير المعلمية.
   - المتوسطات الحسابية والانحرافات المعيارية (Moyennes et Écarts-types): لمعرفة اتجاهات العينة.
   - اختبار معامل الارتباط بيرسون (Corrélation de Pearson): لقياس قوة واتجاه العلاقة بين المتغيرين.
   - تحليل الانحدار الخطي البسيط أو المتعدد (Régression Linéaire): لاختبار الفرضيات وحساب معامل التحديد (R²) ومستوى المعنوية (Sig / p-value).

3. سلم تفسير المتوسطات الحسابية في مقياس ليكرت الخماسي:
   - من 1.00 إلى 1.79: درجة موافقة منخفضة جداً.
   - من 1.80 إلى 2.59: درجة موافقة منخفضة.
   - من 2.60 إلى 3.39: درجة موافقة متوسطة.
   - من 3.40 إلى 4.19: درجة موافقة مرتفعة.
   - من 4.20 إلى 5.00: درجة موافقة مرتفعة جداً.

4. قاعدة قبول أو رفض الفرضية الإحصائية:
   - إذا كانت قيمة الدلالة الإحصائية (Sig. ≤ 0.05): نرفض الفرضية الصفرية H0 ونقبل الفرضية البديلة H1 (أي يوجد أثر ذو دلالة إحصائية).
   - إذا كانت قيمة (Sig. > 0.05): نقبل الفرضية الصفرية H0 ونرفض البديلة.
    """.trimIndent()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "دليل التحليل الإحصائي وتفسير نتائج SPSS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                FilledTonalButton(
                    onClick = { onCopy("دليل التحليل الإحصائي", statsSnippet) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("نسخ الدليل", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                statsSnippet,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DefensePowerPointCard(onCopy: (String, String) -> Unit) {
    val defenseTips = """
🎤 خطة وتوجيهات يوم مناقشة مذكرة التخرج (Soutenance de Mémoire):

1. تقسيم الوقت المثالي (20 دقيقة عرض):
   - الدقائق 1-3: الترحيب باللجنة، دوافع اختيار الموضوع، وأهميته.
   - الدقائق 4-7: طرح الإشكالية، الأسئلة الفرعية، ونموذج الفرضيات.
   - الدقائق 8-11: منهجية الدراسة الميدانية وخصائص العينة ومجتمع الدراسة.
   - الدقائق 12-16: نتائج اختبار الفرضيات بالجداول الإحصائية وتفسيرها.
   - الدقائق 17-19: الاستنتاجات والتوصيات الموجهة للمؤسسة والآفاق المستقبلية.
   - الدقيقة 20: كلمة الختام وشكر لجنة التحكيم وإحالة الكلمة للسيد رئيس اللجنة.

2. إتيكيت وبروتوكول التعامل مع لجنة التحكيم:
   - الوقوف باستقامة وثقة مع توزيع النظرات بين أعضاء اللجنة والحضور.
   - احرص على تدوين كل ملاحظة أو سؤال يطرحه المناقشون في دفتر خاص مع ذكر اسم الأستاذ.
   - عند الإجابة، ابدأ دائماً بـ: "شكراً لأستاذنا الفاضل على هذه الملاحظة القيمة التي ستثري البحث بلا شك...".
   - لا تنفعل ولا تدخل في صدام شخصي؛ اعترف بالهفوات المطبعية بلباقة ودافع عن الجوانب المنهجية بالأدلة والبراهين.
    """.trimIndent()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "بروتوكول ونصائح يوم المناقشة أمام اللجنة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                FilledTonalButton(
                    onClick = { onCopy("نصائح يوم المناقشة", defenseTips) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("نسخ النصائح", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                defenseTips,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
