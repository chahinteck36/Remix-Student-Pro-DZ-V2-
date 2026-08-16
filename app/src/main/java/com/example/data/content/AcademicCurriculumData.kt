package com.example.data.content

import com.example.data.local.ModuleGradeItem

data class SpecialtyPreset(
    val id: String,
    val nameAr: String,
    val facultyAr: String,
    val descriptionAr: String,
    val modules: List<ModuleGradeItem>
)

object AcademicCurriculumData {

    val presets = listOf(
        SpecialtyPreset(
            id = "info_lmd",
            nameAr = "إعلام آلي (LMD Informatique)",
            facultyAr = "كلية العلوم والذكاء الاصطناعي",
            descriptionAr = "مخطط دراسي معياري من السداسي 1 إلى السداسي 6 لتخصص الإعلام الآلي",
            modules = listOf(
                // S1
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "الخوارزميات وهياكل البيانات 1 (ALGO)", unitType = "وحدة أساسية (UEF)", coeff = 4.0, credit = 6, examGrade = 14.0, tdGrade = 15.0, tpGrade = 16.0, hasTp = true),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "التحليل الرياضي 1 (Analyse 1)", unitType = "وحدة أساسية (UEF)", coeff = 4.0, credit = 6, examGrade = 13.0, tdGrade = 14.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "الجبر الخطي 1 (Algèbre 1)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 5, examGrade = 12.5, tdGrade = 13.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "تركيب الحاسوب (Architecture des ordinateurs)", unitType = "وحدة منهجية (UEM)", coeff = 3.0, credit = 5, examGrade = 15.0, tdGrade = 15.0, tpGrade = 16.0, hasTp = true),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "الفيزياء الكهربائية (Electricité)", unitType = "وحدة استكشافية (UED)", coeff = 2.0, credit = 4, examGrade = 12.0, tdGrade = 13.0, tpGrade = 13.0, hasTp = true),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "اللغة الإنجليزية التقنية 1 (Anglais)", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 16.0, tdGrade = 16.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "المصطلحات والمكتبية (Bureautique & TICE)", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 15.5, tdGrade = 15.5, tpGrade = 0.0, hasTp = false),

                // S2
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "الخوارزميات وهياكل البيانات 2 (ALGO 2)", unitType = "وحدة أساسية (UEF)", coeff = 4.0, credit = 6, examGrade = 14.5, tdGrade = 15.5, tpGrade = 16.0, hasTp = true),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "التحليل الرياضي 2 (Analyse 2)", unitType = "وحدة أساسية (UEF)", coeff = 4.0, credit = 6, examGrade = 13.0, tdGrade = 13.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "الاحتمالات والإحصاء (Probabilités & Statistique)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 5, examGrade = 14.0, tdGrade = 14.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "أنظمة التشغيل 1 (Systèmes d'exploitation 1)", unitType = "وحدة منهجية (UEM)", coeff = 3.0, credit = 5, examGrade = 14.0, tdGrade = 15.0, tpGrade = 15.5, hasTp = true),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "تكنولوجيات الويب (Web Tools)", unitType = "وحدة استكشافية (UED)", coeff = 2.0, credit = 4, examGrade = 16.0, tdGrade = 16.5, tpGrade = 17.0, hasTp = true),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "اللغة الإنجليزية 2 (Anglais 2)", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 16.5, tdGrade = 16.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "تاريخ العلوم وتكنولوجيا المعلومات", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 15.0, tdGrade = 15.0, tpGrade = 0.0, hasTp = false),

                // S3
                ModuleGradeItem(academicYear = 2, semester = 3, moduleName = "البرمجة كائنية التوجه (POO / Java)", unitType = "وحدة أساسية (UEF)", coeff = 4.0, credit = 6, examGrade = 15.0, tdGrade = 16.0, tpGrade = 17.0, hasTp = true),
                ModuleGradeItem(academicYear = 2, semester = 3, moduleName = "قواعد البيانات (Bases de Données)", unitType = "وحدة أساسية (UEF)", coeff = 4.0, credit = 6, examGrade = 15.5, tdGrade = 16.0, tpGrade = 16.5, hasTp = true),
                ModuleGradeItem(academicYear = 2, semester = 3, moduleName = "أنظمة التشغيل 2 (Systèmes d'exploitation 2)", unitType = "وحدة منهجية (UEM)", coeff = 3.0, credit = 5, examGrade = 13.5, tdGrade = 14.0, tpGrade = 14.5, hasTp = true),
                ModuleGradeItem(academicYear = 2, semester = 3, moduleName = "المنطق الرياضي ونظرية الرسوم (Logique & Graphes)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 5, examGrade = 13.0, tdGrade = 13.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 2, semester = 3, moduleName = "التحليل العددي (Analyse Numérique)", unitType = "وحدة منهجية (UEM)", coeff = 2.0, credit = 4, examGrade = 12.5, tdGrade = 13.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 2, semester = 3, moduleName = "اللغة الإنجليزية التقنية 3 (English 3)", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 17.0, tdGrade = 17.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 2, semester = 3, moduleName = "أخلاقيات المهنة والحقوق الرقمية", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 16.0, tdGrade = 16.0, tpGrade = 0.0, hasTp = false),

                // S4
                ModuleGradeItem(academicYear = 2, semester = 4, moduleName = "شبكات الحاسوب (Réseaux)", unitType = "وحدة أساسية (UEF)", coeff = 4.0, credit = 6, examGrade = 14.0, tdGrade = 15.0, tpGrade = 15.5, hasTp = true),
                ModuleGradeItem(academicYear = 2, semester = 4, moduleName = "هندسة البرمجيات (Génie Logiciel)", unitType = "وحدة أساسية (UEF)", coeff = 4.0, credit = 6, examGrade = 15.0, tdGrade = 15.5, tpGrade = 16.0, hasTp = true),
                ModuleGradeItem(academicYear = 2, semester = 4, moduleName = "نظرية اللغات والمترجمات (Théorie des Langages)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 5, examGrade = 13.0, tdGrade = 14.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 2, semester = 4, moduleName = "تطوير تطبيقات الويب والهواتف", unitType = "وحدة منهجية (UEM)", coeff = 3.0, credit = 5, examGrade = 16.0, tdGrade = 16.5, tpGrade = 17.5, hasTp = true),
                ModuleGradeItem(academicYear = 2, semester = 4, moduleName = "الأمن المعلوماتي والتشفير", unitType = "وحدة استكشافية (UED)", coeff = 2.0, credit = 4, examGrade = 14.0, tdGrade = 14.5, tpGrade = 15.0, hasTp = true),
                ModuleGradeItem(academicYear = 2, semester = 4, moduleName = "اللغة الإنجليزية 4 (English 4)", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 16.5, tdGrade = 16.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 2, semester = 4, moduleName = "إدارة وتسيير المشاريع", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 15.5, tdGrade = 15.5, tpGrade = 0.0, hasTp = false),

                // S5
                ModuleGradeItem(academicYear = 3, semester = 5, moduleName = "الذكاء الاصطناعي (Intelligence Artificielle)", unitType = "وحدة أساسية (UEF)", coeff = 4.0, credit = 6, examGrade = 15.5, tdGrade = 16.0, tpGrade = 17.0, hasTp = true),
                ModuleGradeItem(academicYear = 3, semester = 5, moduleName = "الأنظمة الموزعة (Systèmes Distribués)", unitType = "وحدة أساسية (UEF)", coeff = 4.0, credit = 6, examGrade = 14.0, tdGrade = 14.5, tpGrade = 15.0, hasTp = true),
                ModuleGradeItem(academicYear = 3, semester = 5, moduleName = "منهجية البحث العلمي وإعداد المذكرة", unitType = "وحدة منهجية (UEM)", coeff = 2.0, credit = 4, examGrade = 15.0, tdGrade = 16.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 3, semester = 5, moduleName = "بحوث العمليات والبرمجة الخطية (RO)", unitType = "وحدة منهجية (UEM)", coeff = 3.0, credit = 5, examGrade = 13.0, tdGrade = 13.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 3, semester = 5, moduleName = "تنقيب البيانات والبيانات الضخمة (Big Data)", unitType = "وحدة استكشافية (UED)", coeff = 2.0, credit = 5, examGrade = 14.5, tdGrade = 15.0, tpGrade = 16.0, hasTp = true),
                ModuleGradeItem(academicYear = 3, semester = 5, moduleName = "ريادة الأعمال والشركات الناشئة", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 16.0, tdGrade = 16.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 3, semester = 5, moduleName = "اللغة الإنجليزية المتخصصة", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 17.0, tdGrade = 17.0, tpGrade = 0.0, hasTp = false),

                // S6
                ModuleGradeItem(academicYear = 3, semester = 6, moduleName = "مشروع تخرج الليسانس ومذكرة PFE", unitType = "وحدة أساسية (UEF)", coeff = 6.0, credit = 14, examGrade = 16.0, tdGrade = 16.5, tpGrade = 0.0, hasTp = false, examWeight = 0.7),
                ModuleGradeItem(academicYear = 3, semester = 6, moduleName = "أمن الشبكات والمعلومات المتقدم", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 14.5, tdGrade = 15.0, tpGrade = 15.5, hasTp = true),
                ModuleGradeItem(academicYear = 3, semester = 6, moduleName = "الحوسبة السحابية وإنترنت الأشياء (Cloud & IoT)", unitType = "وحدة منهجية (UEM)", coeff = 3.0, credit = 6, examGrade = 15.0, tdGrade = 15.5, tpGrade = 16.0, hasTp = true),
                ModuleGradeItem(academicYear = 3, semester = 6, moduleName = "الملكية الفكرية وبراءات الاختراع", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 16.0, tdGrade = 16.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 3, semester = 6, moduleName = "تقنيات التقديم الشفهي ومناقشة المذكرة", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 17.0, tdGrade = 17.0, tpGrade = 0.0, hasTp = false)
            )
        ),
        SpecialtyPreset(
            id = "st_tech",
            nameAr = "علوم وتكنولوجيا (Sciences et Technologies - ST)",
            facultyAr = "كلية التكنولوجيا والهندسة",
            descriptionAr = "مخطط جذع مشترك وتخصصات الهندسة (كهرباء، ميكانيك، مدني، صناعي)",
            modules = listOf(
                // S1
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "الرياضيات 1 (Mathématiques 1)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 12.5, tdGrade = 13.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "الفيزياء 1 - ميكانيك النقطة المادية (Physique 1)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 13.0, tdGrade = 14.0, tpGrade = 14.5, hasTp = true),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "الكيمياء 1 - بنية المادة (Chimie 1)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 12.0, tdGrade = 13.0, tpGrade = 14.0, hasTp = true),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "الإعلام الآلي 1 (Informatique 1)", unitType = "وحدة منهجية (UEM)", coeff = 2.0, credit = 4, examGrade = 15.0, tdGrade = 16.0, tpGrade = 16.5, hasTp = true),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "المنهجية وتاريخ العلوم والتكنولوجيا (MST)", unitType = "وحدة استكشافية (UED)", coeff = 1.0, credit = 2, examGrade = 14.0, tdGrade = 14.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "اللغة الإنجليزية 1 (Anglais 1)", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 15.5, tdGrade = 15.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "اللغة الفرنسية والمصطلحات (Français)", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 14.5, tdGrade = 14.5, tpGrade = 0.0, hasTp = false),

                // S2
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "الرياضيات 2 (Mathématiques 2)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 13.0, tdGrade = 14.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "الفيزياء 2 - الكهرباء والمغناطيسية (Physique 2)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 13.5, tdGrade = 14.0, tpGrade = 15.0, hasTp = true),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "الكيمياء 2 - الترموديناميك الكيميائي (Chimie 2)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 12.5, tdGrade = 13.5, tpGrade = 14.0, hasTp = true),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "الإعلام الآلي 2 والبرمجة (Informatique 2)", unitType = "وحدة منهجية (UEM)", coeff = 2.0, credit = 4, examGrade = 15.5, tdGrade = 16.0, tpGrade = 16.5, hasTp = true),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "مدخل إلى الهندسة الكهربائية والميكانيكية", unitType = "وحدة استكشافية (UED)", coeff = 1.0, credit = 2, examGrade = 14.0, tdGrade = 14.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "اللغة الإنجليزية 2 (Anglais 2)", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 16.0, tdGrade = 16.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "أخلاقيات المهنة الجامعية (Déontologie)", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 15.0, tdGrade = 15.0, tpGrade = 0.0, hasTp = false)
            )
        ),
        SpecialtyPreset(
            id = "eco_gest",
            nameAr = "علوم اقتصادية وتجارية وعلوم التسيير (SEGC)",
            facultyAr = "كلية العلوم الاقتصادية والتجارية",
            descriptionAr = "مخطط العلوم الاقتصادية، المالية، المحاسبة، وإدارة الأعمال",
            modules = listOf(
                // S1
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "مدخل إلى علم الاقتصاد (Introduction à l'Economie)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 13.5, tdGrade = 14.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "المحاسبة المالية 1 (Comptabilité Financière 1)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 14.0, tdGrade = 15.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "الرياضيات المالية والإحصاء الوصفي (Maths & Stat)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 13.0, tdGrade = 14.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "مدخل إلى إدارة الأعمال والتسيير (Management)", unitType = "وحدة منهجية (UEM)", coeff = 2.0, credit = 4, examGrade = 15.0, tdGrade = 15.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "مدخل للقانون والقانون التجاري (Droit)", unitType = "وحدة استكشافية (UED)", coeff = 2.0, credit = 4, examGrade = 13.0, tdGrade = 13.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "اللغة الإنجليزية الاقتصادية 1 (English for Business)", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 16.0, tdGrade = 16.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "تاريخ الفكر الاقتصادي والوقائع", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 14.5, tdGrade = 14.5, tpGrade = 0.0, hasTp = false),

                // S2
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "الاقتصاد الجزئي 1 (Microéconomie 1)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 14.0, tdGrade = 14.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "المحاسبة المالية 2 (Comptabilité Financière 2)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 14.5, tdGrade = 15.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "الإحصاء الاستدلالي والاحتمالات (Statistique 2)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 13.5, tdGrade = 14.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "اقتصاد المؤسسة واستراتيجيات الأعمال", unitType = "وحدة منهجية (UEM)", coeff = 2.0, credit = 4, examGrade = 15.0, tdGrade = 15.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "الإعلام الآلي وتطبيقات Excel في التسيير", unitType = "وحدة استكشافية (UED)", coeff = 2.0, credit = 4, examGrade = 16.0, tdGrade = 16.5, tpGrade = 17.0, hasTp = true),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "اللغة الإنجليزية الاقتصادية 2", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 16.0, tdGrade = 16.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 2, moduleName = "منهجية البحث في العلوم الاقتصادية", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 15.5, tdGrade = 15.5, tpGrade = 0.0, hasTp = false)
            )
        ),
        SpecialtyPreset(
            id = "droit_pol",
            nameAr = "الحقوق والعلوم السياسية (Droit & Sciences Politiques)",
            facultyAr = "كلية الحقوق والعلوم السياسية",
            descriptionAr = "مخطط القانون العام والخاص والعلوم الإدارية والسياسية",
            modules = listOf(
                // S1
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "المدخل للعلوم القانونية (نظرية القانون)", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 13.0, tdGrade = 14.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "القانون الدستوري والأنظمة السياسية المقارنة", unitType = "وحدة أساسية (UEF)", coeff = 3.0, credit = 6, examGrade = 13.5, tdGrade = 14.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "تاريخ النظم القانونية والمؤسسات", unitType = "وحدة أساسية (UEF)", coeff = 2.0, credit = 5, examGrade = 14.0, tdGrade = 14.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "مدخل إلى الشريعة الإسلامية ومصادر الفقه", unitType = "وحدة منهجية (UEM)", coeff = 2.0, credit = 5, examGrade = 15.0, tdGrade = 15.5, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "منهجية العلوم القانونية وتقنيات البحث", unitType = "وحدة منهجية (UEM)", coeff = 2.0, credit = 4, examGrade = 14.5, tdGrade = 15.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "المصطلحات القانونية باللغة الأجنبية 1", unitType = "وحدة أفقية (UET)", coeff = 1.0, credit = 2, examGrade = 16.0, tdGrade = 16.0, tpGrade = 0.0, hasTp = false),
                ModuleGradeItem(academicYear = 1, semester = 1, moduleName = "المجتمع الدولي والعلاقات الدولية", unitType = "وحدة استكشافية (UED)", coeff = 1.0, credit = 2, examGrade = 14.0, tdGrade = 14.0, tpGrade = 0.0, hasTp = false)
            )
        )
    )

    val unitTypes = listOf(
        "وحدة أساسية (UEF)",
        "وحدة منهجية (UEM)",
        "وحدة استكشافية (UED)",
        "وحدة أفقية (UET)"
    )

    val academicLevels = listOf(
        "السنة الأولى ليسانس (L1 / Tronc Commun)",
        "السنة الثانية ليسانس (L2 / Spécialité)",
        "السنة الثالثة ليسانس (L3 / Diplôme de Licence)",
        "السنة الأولى ماستر (Master 1)",
        "السنة الثانية ماستر (Master 2 & PFE)",
        "دراسات عليا / دكتوراه / أخرى"
    )

    fun getAcademicMention(average: Double): Pair<String, String> {
        return when {
            average >= 16.0 -> "ممتاز (Très Bien / Excellent)" to "تهانينا! تفوق أكاديمي استثنائي يؤهلك للمرتبة الأولى (Major de promotion)."
            average >= 14.0 -> "جيد جداً (Bien)" to "معدل ممتاز يمنحك أفضلية مطلقة في مسابقات الماستر والدكتوراه والمنح الدولية."
            average >= 12.0 -> "جيد (Assez Bien)" to "أداء أكاديمي متفوق ومستقر مع تحصيل كامل للأرصدة."
            average >= 11.0 -> "قريب من الجيد (Passable+)" to "مسار أكاديمي ناجح ومستوفى بنجاح."
            average >= 10.0 -> "مقبول (Passable)" to "تم استيفاء متطلبات النجاح والانتقال بنجاح."
            average >= 9.0 -> "مؤهل للاستدراك / مداولات الإنقاذ" to "يمكنك التعويض بالسنوات الأخرى أو استدراك بعض المقاييس لرفع المعدل."
            else -> "غير مستوفى (Ajourné)" to "يحتاج إلى مراجعة مكثفة وخطة دعم في المواد ذات المعاملات المرتفعة."
        }
    }

    fun getLmdCategory(average: Double, hasRattrapage: Boolean = false): Pair<String, String> {
        return when {
            average >= 15.0 && !hasRattrapage -> "فئة (A) - النخبة الأكاديمية" to "أعلى 10% من الدفعة - أولوية قصوى في مسابقات الدكتوراه والتوظيف الجامعي."
            average >= 13.0 -> "فئة (B) - متفوق" to "أعلى 25% من الدفعة - تنقيط ممتاز في ملفات الترشح."
            average >= 11.0 -> "فئة (C) - متوسط متقدم" to "أعلى 50% من الدفعة - مستوفى بانتظام."
            average >= 10.0 -> "فئة (D) - مقبول" to "استيفاء شروط التخرج والحصول على الشهادة."
            else -> "فئة (E) - دورة الاستدراك" to "تخرج عبر الدورة الاستدراكية."
        }
    }
}
