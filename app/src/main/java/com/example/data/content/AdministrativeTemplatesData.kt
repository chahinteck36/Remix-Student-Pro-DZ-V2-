package com.example.data.content

import com.example.data.local.UserProfile

data class AdminDocumentTemplate(
    val id: String,
    val title: String,
    val language: String, // "عربي", "Français"
    val category: String, // "التربصات والتكوين", "التسجيل والبيداغوجيا", "مراسلات الأساتذة", "التخرج والماستر", "الخدمات الجامعية"
    val description: String,
    val tag: String = "رسمي",
    val requiredFields: List<FormFieldConfig>,
    val generateContent: (UserProfile, Map<String, String>) -> String
)

data class FormFieldConfig(
    val key: String,
    val label: String,
    val defaultValue: String = "",
    val placeholder: String = ""
)

object AdministrativeTemplatesData {

    val categories = listOf(
        "الكل",
        "التربصات والتكوين",
        "التسجيل والبيداغوجيا",
        "مراسلات الأساتذة",
        "التخرج والماستر",
        "الخدمات الجامعية"
    )

    val templates = listOf(
        // ==================== 1. INTERNSHIPS & PRACTICAL TRAINING ====================
        AdminDocumentTemplate(
            id = "stage_request_ar",
            title = "طلب إجراء تربص ميداني (بالعربية)",
            language = "عربي",
            category = "التربصات والتكوين",
            tag = "طلب رسمي",
            description = "طلب موجه لمدير المؤسسة أو الشركة للموافقة على فترة تدريب ميداني جامعي",
            requiredFields = listOf(
                FormFieldConfig("company_name", "اسم الشركة أو المؤسسة المستقبلة", "مؤسسة نفطال / المديرية العامة", "مثال: بنك الجزائر، مجمع سوناطراك..."),
                FormFieldConfig("director_title", "صفة المسؤول", "السيد مدير الموارد البشرية والتدريب", "مثال: السيد المدير العام"),
                FormFieldConfig("stage_period", "فترة التربص المحددة", "من 01 أفريل 2026 إلى 31 ماي 2026", "حدد تاريخ البداية والنهاية"),
                FormFieldConfig("stage_topic", "موضوع أو هدف التربص", "إعداد الجانب التطبيقي لمذكرة التخرج ومطابقة النظري بالتطبيقي", "موضوع البحث أو مجال التدريب")
            ),
            generateContent = { user, extra ->
                """
الجمهورية الجزائرية الديمقراطية الشعبية
وزارة التعليم العالي والبحث العلمي
${user.university}
${user.faculty}
${user.department}

إلى السيد: ${extra["director_title"] ?: "المدير العام المحترم"}
مؤسسة / شركة: ${extra["company_name"] ?: "المؤسسة المستقبلة"}

الموضوع: طلب إجراء تربص ميداني جامعي
المرفقات: نسخة من بطاقة الطالب + استمارة التأمين + اتفاقية التربص الثلاثية

يشرفني أن أتوجه إلى سيادتكم المحترمة بهذا الطلب قصد التكرم بمنحي الموافقة على إجراء تربص ميداني في مؤسستكم الموقرة خلال الفترة الممتدة: ${extra["stage_period"] ?: "المحددة أعلاه"}.

أحيطكم علماً بأنني الطالب(ة): ${user.fullName}
المسجل(ة) بـ: ${user.university}
المستوى والتخصص: ${user.academicLevel} - ${user.specialty}
رقم التسجيل الجامعي: ${user.studentIdNumber}

يهدف هذا التربص إلى ${extra["stage_topic"] ?: "إسقاط المعارف النظرية على الواقع المهني وإعداد مشروع التخرج"}، والاستفادة من الخبرات القيمة التي تزخر بها مؤسستكم الرائدة.

نلتزم باحترام النظام الداخلي للمؤسسة والحفاظ على سرية المعلومات المهنية.

وفي انتظار ردكم الإيجابي، تفضلوا سيدي بقبول فائق عبارات التقدير والاحترام.

حرر بـ: الجزائر في [تاريخ اليوم]
إمضاء الطالب(ة):
${user.fullName}
الهاتف: ${user.phone}
البريد الإلكتروني: ${user.email}
                """.trimIndent()
            }
        ),
        AdminDocumentTemplate(
            id = "stage_request_fr",
            title = "Demande de Stage Pratique (En Français)",
            language = "Français",
            category = "التربصات والتكوين",
            tag = "Forme Officielle",
            description = "Lettre administrative officielle en français pour demande de stage en entreprise",
            requiredFields = listOf(
                FormFieldConfig("company_name", "Nom de l'entreprise", "Sonatrach / Direction Générale", "Ex: Ooredoo, BNA Bank..."),
                FormFieldConfig("director_title", "Titre du destinataire", "Monsieur le Directeur des Ressources Humaines", "Ex: Monsieur le Directeur Général"),
                FormFieldConfig("stage_period", "Période du stage", "Du 15 Mars au 15 Mai 2026", "Durée du stage"),
                FormFieldConfig("stage_topic", "Thème / Objectif", "Collecte des données pour le mémoire de fin d'études", "Objectif du stage")
            ),
            generateContent = { user, extra ->
                """
RÉPUBLIQUE ALGÉRIENNE DÉMOCRATIQUE ET POPULAIRE
Ministère de l'Enseignement Supérieur et de la Recherche Scientifique
${user.university}
${user.faculty}

Nom et Prénom: ${user.fullName}
Niveau d'études: ${user.academicLevel}
Spécialité: ${user.specialty}
N° d'inscription: ${user.studentIdNumber}
Tél: ${user.phone} | E-mail: ${user.email}

À l'attention de: ${extra["director_title"] ?: "Monsieur le Directeur"}
Entreprise: ${extra["company_name"] ?: "L'Entreprise"}

Objet: Demande de stage pratique
P.J: Convention tripartite de stage, Attestation d'assurance, Copie de la carte d'étudiant

Monsieur le Directeur,

Actuellement étudiant(e) en ${user.academicLevel} (Option: ${user.specialty}) au sein de ${user.university}, j'ai l'honneur de solliciter votre bienveillance afin de m'accorder un stage pratique au sein de votre honorable organisme pour la période: ${extra["stage_period"] ?: "mentionnée"}.

Ce stage a pour objectif de: ${extra["stage_topic"] ?: "perfectionner mes compétences et préparer mon projet de fin d'études"}.

Convaincu(e) que votre entreprise représente le cadre idéal pour acquérir une expérience professionnelle de premier ordre, j'espère vivement que ma demande retiendra votre attention favorable.

Dans l'attente d'une réponse positive, je vous prie d'agréer, Monsieur le Directeur, l'expression de mes salutations les plus distinguées.

Fait à Alger, le [Date du jour]
Signature de l'étudiant:
${user.fullName}
                """.trimIndent()
            }
        ),
        AdminDocumentTemplate(
            id = "stage_convention_ar",
            title = "ملخص اتفاقية التربص الميداني (Convention)",
            language = "عربي",
            category = "التربصات والتكوين",
            tag = "اتفاقية ثلاثية",
            description = "بنود اتفاقية التربص الثلاثية المبرمة بين الجامعة والمؤسسة المستقبلة والطالب",
            requiredFields = listOf(
                FormFieldConfig("company_name", "المؤسسة المستقبلة", "الشركة الوطنية للكهرباء والغاز (سونلغاز)", "اسم الهيئة المستقبلة"),
                FormFieldConfig("company_tutor", "مؤطر المؤسسة (Tuteur)", "السيد رئيس مصلحة نظم المعلومات", "اسم وصفة المؤطر الميداني"),
                FormFieldConfig("academic_tutor", "المشرف البيداغوجي بالجامعة", "الدكتور المشرف على المذكرة", "اسم الأستاذ الجامعي المشرف"),
                FormFieldConfig("stage_duration", "مدة وساعات التربص", "شهرين بمعدل 4 أيام في الأسبوع (240 ساعة إجمالية)", "مدة وساعات الحضور")
            ),
            generateContent = { user, extra ->
                """
اتفاقية تربص ميداني جامعي
(وفقاً للقرار الوزاري المحدد لكيفيات التربصات الميدانية في الوسط المهني)

بين الأطراف المتعاقدة:
1. الطرف الأول (المؤسسة الجامعية): ${user.university} - ممثلة برئيس القسم: ${user.department}.
2. الطرف الثاني (المؤسسة المستقبلة): ${extra["company_name"] ?: "الهيئة المستقبلة"}.
3. الطرف الثالث (المتربص): الطالب(ة) ${user.fullName}، رقم التسجيل: ${user.studentIdNumber}، التخصص: ${user.specialty}.

المادة 1 - موضوع الاتفاقية:
تهدف هذه الاتفاقية إلى تنظيم تربص ميداني تطبيقي للطالب لتعزيز تكوينه الأكاديمي.

المادة 2 - التأطير والمتابعة:
- المؤطر الميداني بالمؤسسة: ${extra["company_tutor"] ?: "المسؤول المعين من المؤسسة"}.
- المشرف البيداغوجي بالجامعة: ${extra["academic_tutor"] ?: "الأستاذ المشرف"}.

المادة 3 - الالتزامات والتأمين:
- يلتزم الطالب باحترام مواعيد العمل والنظام الداخلي والسر المهني للمؤسسة.
- تغطي الجامعة التأمين عن حوادث العمل خلال كامل فترة التربص (${extra["stage_duration"] ?: "المتفق عليها"}).

حرر في ثلاث نسخ أصلية.
إمضاء الطالب: ${user.fullName} | إمضاء عميد الكلية | إمضاء وختم مدير المؤسسة
                """.trimIndent()
            }
        ),

        // ==================== 2. ACADEMIC & COURSE REGISTRATION ====================
        AdminDocumentTemplate(
            id = "course_registration_ar",
            title = "استمارة إعادة التسجيل واختيار التخصص / الفوج",
            language = "عربي",
            category = "التسجيل والبيداغوجيا",
            tag = "بيداغوجي",
            description = "استمارة رسمية لتأكيد التسجيل السنوي واختيار المسار أو طلب تغيير الفوج",
            requiredFields = listOf(
                FormFieldConfig("target_semester", "السداسي / السنة المراد التسجيل فيها", "السنة الثالثة ليسانس (L3) - السداسي 5", "مثال: L2 S3، M1 S1"),
                FormFieldConfig("desired_group", "الفوج المطلوب (Groupe TD/TP)", "الفوج رقم 03 (Groupe 03)", "حدد الفوج المرغوب"),
                FormFieldConfig("reason_choice", "مبرر طلب الفوج أو التخصص", "التوافق مع جدول الحصص والالتزامات البيداغوجية", "سبب الاختيار")
            ),
            generateContent = { user, extra ->
                """
الجمهورية الجزائرية الديمقراطية الشعبية
وزارة التعليم العالي والبحث العلمي
${user.university}
${user.faculty} - مصلحة التدريس والشؤون البيداغوجية

استمارة إعادة التسجيل وتأكيد المسار البيداغوجي
السنة الجامعية: 2025 / 2026

1. معلومات الطالب:
- الاسم واللقب: ${user.fullName}
- تاريخ ومكان الازدياد: [اليوم / الشهر / السنة] بـ [الولاية]
- رقم التسجيل الجامعي: ${user.studentIdNumber}
- الكلية / القسم: ${user.faculty} - ${user.department}
- التخصص: ${user.specialty}
- المستوى الأكاديمي: ${extra["target_semester"] ?: user.academicLevel}

2. الخيارات البيداغوجية المطلوبة:
- الفوج المقترح: ${extra["desired_group"] ?: "الفوج المحدد"}
- المبرر: ${extra["reason_choice"] ?: "التنظيم البيداغوجي ومتابعة المواد"}

3. تعهد الطالب:
أصرح بشرفي بصحة المعلومات الواردة أعلاه وألتزم بالانضباط وحضور كافة الحصص المقررة.

تاريخ التقديم: [التاريخ]
إمضاء الطالب: ${user.fullName}
رأي وتأشيرة رئيس القسم / مصلحة البيداغوجيا:
[مقبول / مرفوض] - الختم الرسمي:
                """.trimIndent()
            }
        ),
        AdminDocumentTemplate(
            id = "recours_note_ar",
            title = "استمارة الطعن في علامة الامتحان / المداولات",
            language = "عربي",
            category = "التسجيل والبيداغوجيا",
            tag = "طعن رسمي",
            description = "طلب مراجعة ورقة الامتحان أو خطأ في نقل علامات المداولات الرسمية",
            requiredFields = listOf(
                FormFieldConfig("module_name", "اسم المقياس موضوع الطعن", "الخوارزميات وهياكل البيانات (Algorithmique)", "اسم المادة بدقة"),
                FormFieldConfig("exam_session", "طبيعة الامتحان", "امتحان الدورة العادية - السداسي الأول (S1)", "عادية / استدراك"),
                FormFieldConfig("current_grade", "العلامة الممنوحة حالياً", "07.50 / 20.00", "العلامة المعلنة"),
                FormFieldConfig("recours_reason", "سبب الطعن والاعتراض", "خطأ في جمع النقاط ومطابقة الإجابة النموذجية للتمرين الثاني", "حدد سبب الاعتراض بالتفصيل")
            ),
            generateContent = { user, extra ->
                """
الجمهورية الجزائرية الديمقراطية الشعبية
${user.university}
${user.faculty}
قسم: ${user.department}

إلى السيد: رئيس القسم / رئيس اللجنة البيداغوجية
عبر السلم الإداري

الموضوع: استمارة طعن ومراجعة علامة امتحان
المقياس المعني: ${extra["module_name"] ?: "المقياس المعني"}
الدورة: ${extra["exam_session"] ?: "الدورة العادية"}

يشرفني أن أرفع إلى عنايتكم الكريمة هذا الطعن بخصوص علامة مقياس "${extra["module_name"] ?: "المحدد"}".

بيانات الطالب(ة):
- الاسم واللقب: ${user.fullName}
- المستوى والتخصص: ${user.academicLevel} - ${user.specialty}
- الفوج: [رقم الفوج] | رقم التسجيل: ${user.studentIdNumber}
- العلامة المعلنة في كشف المداولات: ${extra["current_grade"] ?: "العلامة المسجلة"}

أسباب الطعن والمراجعة:
${extra["recours_reason"] ?: "أرجو التكرم بإعادة تدقيق جمع النقاط والتصحيح البيداغوجي لورقة الامتحان ومقارنتها بالإجابة النموذجية."}

ألتمس من سيادتكم تمكيني من الإطلاع على ورقة امتحاني أو إعادة تصحيحها وفق التنظيم المعمول به.

تقبلوا فائق عبارات الاحترام والتقدير.
حرر في: [تاريخ إيداع الطعن]
إمضاء الطالب(ة): ${user.fullName}
                """.trimIndent()
            }
        ),
        AdminDocumentTemplate(
            id = "conge_academique",
            title = "طلب عطلة أكاديمية (تجميد السنة الجامعية)",
            language = "عربي",
            category = "التسجيل والبيداغوجيا",
            tag = "طلب رسمي",
            description = "طلب رسمي لرئيس القسم وعميد الكلية لتجميد السنة الجامعية لأسباب مبررة وقاهرة",
            requiredFields = listOf(
                FormFieldConfig("academic_year", "السنة الجامعية المراد تجميدها", "2025 / 2026", "مثال: 2025/2026"),
                FormFieldConfig("reason", "سبب طلب العطلة الأكاديمية", "أسباب صحية وعائلية قاهرة تمنعني من المواظبة على الدروس", "حدد السبب: صحي، عائلي، التزامات قاهرة..."),
                FormFieldConfig("attached_proof", "الوثائق المبررة المرفقة", "شهادة طبية معتمدة من طبيب محلف + كشف نقاط البكالوريا", "الوثائق المرفقة كدليل")
            ),
            generateContent = { user, extra ->
                """
الجمهورية الجزائرية الديمقراطية الشعبية
وزارة التعليم العالي والبحث العلمي
${user.university}
${user.faculty}
${user.department}

إلى السيد: نائب العميد المكلف بالدراسات والشؤون البيداغوجية
عبر السيد رئيس القسم

الموضوع: طلب الاستفادة من عطلة أكاديمية (تجميد السنة الجامعية)
السنة الجامعية: ${extra["academic_year"] ?: "2025/2026"}
المرفقات: ${extra["attached_proof"] ?: "الوثائق والمبررات الرسمية المعتمدة"}

يشرفني أن أتقدم إلى سيادتكم المحترمة بهذا الطلب ملتمساً منكم الموافقة على منحي عطلة أكاديمية للسنة الجامعية ${extra["academic_year"] ?: "الحالية"}.

أحيطكم علماً بأنني الطالب(ة): ${user.fullName}
المسجل(ة) في مستوى: ${user.academicLevel}
تخصص: ${user.specialty}
رقم التسجيل: ${user.studentIdNumber}

يعود سبب طلبي هذا إلى: ${extra["reason"] ?: "ظروف قاهرة مبررة بالوثائق المرفقة"}، والتي تحول دون تمكني من متابعة الدروس والامتحانات بشكل منتظم خلال هذا الموسم.

وتجدون رفقة هذا الطلب كافة الوثائق الإثباتية القانونية الصادرة عن الجهات المختصة.

تقبلوا منا فائق عبارات الاحترام والتقدير.

حرر في [التاريخ]
إمضاء الطالب المعني: ${user.fullName}
                """.trimIndent()
            }
        ),
        AdminDocumentTemplate(
            id = "reintegration_request",
            title = "طلب إعادة إدماج واستئناف الدراسة",
            language = "عربي",
            category = "التسجيل والبيداغوجيا",
            tag = "طلب رسمي",
            description = "موجّه لرئيس القسم لاستئناف الدراسة بعد انقطاع أو عطلة أكاديمية مسبقة",
            requiredFields = listOf(
                FormFieldConfig("interruption_year", "سنة الانقطاع / العطلة السابقة", "2024 / 2025", "السنة التي انقطعت فيها"),
                FormFieldConfig("resume_year", "السنة المراد استئناف الدراسة فيها", "2025 / 2026", "السنة الجامعية الحالية"),
                FormFieldConfig("decision_ref", "رقم وتاريخ قرار العطلة الأكاديمية السابقة", "قرار إداري رقم 142/2024 بتاريخ 10 نوفمبر 2024", "إن وجد")
            ),
            generateContent = { user, extra ->
                """
الجمهورية الجزائرية الديمقراطية الشعبية
${user.university}
${user.faculty}
${user.department}

إلى السيد: رئيس قسم ${user.department}
الموضوع: طلب إعادة الإدماج واستئناف المسار الدراسي
المرفقات: نسخة من قرار العطلة الأكاديمية + كشف النقاط الأخير + شهادة إعادة التسجيل

يشرفني أن أتقدم إلى سيادتكم بطلبي هذا قصد الموافقة على إعادة إدماجي لمواصلة مساري الدراسي خلال السنة الجامعية: ${extra["resume_year"] ?: "2025/2026"}.

معلومات الطالب(ة):
- الاسم واللقب: ${user.fullName}
- التخصص والمستوى: ${user.academicLevel} - ${user.specialty}
- رقم التسجيل: ${user.studentIdNumber}
- المرجع: استفدت سابقاً من عطلة أكاديمية (${extra["decision_ref"] ?: "المرفقة"}).

ونظراً لزوال الأسباب والظروف المانعة، ألتمس من سيادتكم الموافقة على تسجيلي النظامي ضمن قائمة طلبة القسم للسنة الحالية.

شاكراً لكم حسن تعاونكم وتفهمكم، تقبلوا فائق التقدير والاحترام.

إمضاء الطالب: ${user.fullName}
                """.trimIndent()
            }
        ),

        // ==================== 3. PROFESSOR & SUPERVISOR COMMUNICATION DRAFTS ====================
        AdminDocumentTemplate(
            id = "supervisor_encadrement_ar",
            title = "طلب إشراف وتأطير على مذكرة التخرج (PFE)",
            language = "عربي",
            category = "مراسلات الأساتذة",
            tag = "إشراف وتأطير",
            description = "مراسلة أكاديمية للأستاذ لطلب قبوله تأطير والإشراف على مشروع مذكرة التخرج",
            requiredFields = listOf(
                FormFieldConfig("professor_name", "اسم ورتبة الأستاذ", "الأستاذ الدكتور / فلان بن فلان", "مثال: أ.د. أحمد بوزيد"),
                FormFieldConfig("thesis_subject", "عنوان أو فكرة البحث المقترحة", "تطبيق تقنيات تعلم الآلة في التنبؤ المالي للمؤسسات الناشئة", "موضوع المذكرة المقترح"),
                FormFieldConfig("team_members", "أعضاء الفوج (الطلبة)", "الطالب(ة): فلان وفلان", "الأسماء والبيانات"),
                FormFieldConfig("motivation_reason", "سبب اختيار الأستاذ", "نظراً لخبرتكم وأبحاثكم الرائدة في مجال الذكاء الاصطناعي والأنظمة الموزعة", "لماذا تم اختيار الأستاذ")
            ),
            generateContent = { user, extra ->
                """
السلام عليكم ورحمة الله وبركاته،
تحية طيبة وبعد،

إلى الأستاذ الفاضل: ${extra["professor_name"] ?: "الأستاذ المشرف المحترم"}،
قسم: ${user.department} - ${user.faculty}

الموضوع: طلب قبول الإشراف والتأطير الأكاديمي على مذكرة تخرج (${user.academicLevel})

نحن طلبتك في مرحلة ${user.academicLevel} تخصص ${user.specialty}:
- الطالب(ة): ${user.fullName} (رقم التسجيل: ${user.studentIdNumber})
- الزميل(ة) في الفوج: ${extra["team_members"] ?: "فوج البحث"}

يشرفنا ويسعدنا أن نتقدم إلى سيادتكم الكريمة بطلب الموافقة على تأطير والإشراف على مذكرة تخرجنا الموسومة بـ:
"${extra["thesis_subject"] ?: "موضوع المذكرة المقترح"}"

لقد دفعنا لاختيار سيادتكم ${extra["motivation_reason"] ?: "سمعتكم العلمية المرموقة وخبرتكم العميقة في هذا المجال البحثي"}، ونحن على يقين بأن توجيهاتكم وإرشاداتكم ستكون ركيزة نجاح هذا العمل العلمي.

تجدون رفقة هذا الطلب ملخصاً أولياً (Résumé / Abstract) لإشكالية البحث وأهدافه ومنهجيته.

نلتمس منكم التكرم بتحديد موعد لمناقشة الفكرة الأولية.

وتفضلوا بقبول أسمى عبارات الشكر والامتنان.

الطلبة المعنيون:
${user.fullName}
الهاتف: ${user.phone} | البريد الإلكتروني: ${user.email}
                """.trimIndent()
            }
        ),
        AdminDocumentTemplate(
            id = "exam_consultation_ar",
            title = "طلب موعد استشارة بيداغوجية ومراجعة ورقة الامتحان",
            language = "عربي",
            category = "مراسلات الأساتذة",
            tag = "بريد رسمي",
            description = "صيغة بريد إلكتروني محترمة للأستاذ لتحديد موعد الإطلاع على ورقة الامتحان",
            requiredFields = listOf(
                FormFieldConfig("prof_name", "اسم الأستاذ المحترم", "د. محمد الطاهر", "اسم أستاذ المادة"),
                FormFieldConfig("module_name", "اسم المقياس", "مقياس الاقتصاد الجزئي (Microéconomie)", "اسم المقياس"),
                FormFieldConfig("group_num", "الفوج والسداسي", "الفوج 02 - السداسي الأول S1", "الفوج الدراسي"),
                FormFieldConfig("polite_note", "ملاحظة التقدير", "قصد الاستفادة من ملاحظاتكم البيداغوجية وتصحيح الأخطاء المعرفية", "الهدف من الاستشارة")
            ),
            generateContent = { user, extra ->
                """
السلام عليكم ورحمة الله وبركاته،
الأستاذ الفاضل: ${extra["prof_name"] ?: "أستاذ المقياس المحترم"}،

تحية طيبة ملؤها الاحترام والتقدير،

أنا الطالب(ة): ${user.fullName}
المسجل(ة) في مستوى: ${user.academicLevel} - تخصص: ${user.specialty}
(${extra["group_num"] ?: "الفوج الدراسي"}) - رقم التسجيل: ${user.studentIdNumber}

أتوجه إلى سيادتكم بهذا البريد ملتمساً من كرمكم التكرم بتحديد موعد لجلسة الاستشارة البيداغوجية الخاصة بامتحان مقياس: "${extra["module_name"] ?: "المقياس"}"، وذلك ${extra["polite_note"] ?: "للاطلاع على ورقة الإجابة والاستفادة من توجيهاتكم القيمة لمعالجة النقائص في المحطات القادمة"}.

أشكركم جزيل الشكر على جهودكم وسعة صدركم، وفي انتظار ما ترونه مناسباً من موعد.

تلميذكم: ${user.fullName}
البريد: ${user.email} | الهاتف: ${user.phone}
                """.trimIndent()
            }
        ),
        AdminDocumentTemplate(
            id = "absence_justification_ar",
            title = "تبرير غياب رسمي عن حصة تطبيقية (TD/TP) أو امتحان",
            language = "عربي",
            category = "مراسلات الأساتذة",
            tag = "تبرير غياب",
            description = "مراسلة رسمية للأستاذ ومسؤول الفوج لتبرير الغياب وإلغاء الإنذار بالوثائق الثبوتية",
            requiredFields = listOf(
                FormFieldConfig("prof_name", "اسم الأستاذ أو مسؤول المادة", "أستاذ حصة الأعمال الموجهة TD", "اسم الأستاذ"),
                FormFieldConfig("missed_date", "تاريخ وتوقيت الحصة", "يوم الثلاثاء 10 مارس 2026 (08:30 - 10:00)", "تاريخ الغياب"),
                FormFieldConfig("absence_reason", "سبب الغياب", "وعكة صحية طارئة ومفاجئة", "مرضي / التزام عائلي قاهر"),
                FormFieldConfig("attached_justif", "الوثيقة الإثباتية المرفقة", "شهادة طبية رسمية مبررة للغياب صادرة في التاريخ نفسه", "شهادة طبية، استدعاء رسمي...")
            ),
            generateContent = { user, extra ->
                """
الجمهورية الجزائرية الديمقراطية الشعبية
${user.university}
${user.faculty}

إلى السيد(ة) الأستاذ(ة): ${extra["prof_name"] ?: "المشرف(ة) على الحصة"}
الموضوع: تبرير غياب رسمي عن الحصة البيداغوجية
المرفقات: ${extra["attached_justif"] ?: "الشهادة والوثيقة المبررة"}

تحية طيبة وبعد،

أحيطكم علماً بأنني الطالب(ة): ${user.fullName}
المسجل(ة) في الفوج: [رقم الفوج] - تخصص: ${user.specialty} (${user.academicLevel})
رقم التسجيل: ${user.studentIdNumber}

يؤسفني إعلامكم بتعذر حضوري الحصة البيداغوجية المنعقدة بتاريخ: ${extra["missed_date"] ?: "المحدد"}، وذلك بسبب: ${extra["absence_reason"] ?: "ظرف قاهر ومبرر"}.

وتجدون طيه الوثيقة الإثباتية القانونية، راجياً منكم التكرم بقبول التبرير وإلغاء تسجيل الغياب حفاظاً على المسار البيداغوجي.

شاكراً لكم حسن تفهمكم وتعاونكم.

إمضاء الطالب(ة): ${user.fullName}
التاريخ: [تاريخ تقديم التبرير]
                """.trimIndent()
            }
        ),
        AdminDocumentTemplate(
            id = "recommendation_letter_req",
            title = "طلب رسالة توصية أكاديمية (Lettre de Recommandation)",
            language = "عربي",
            category = "مراسلات الأساتذة",
            tag = "توصية أكاديمية",
            description = "نموذج لمراسلة الأستاذ لطلب منحه رسالة تزكية وتوصية للماستر أو الدكتوراه",
            requiredFields = listOf(
                FormFieldConfig("prof_title", "اسم الأستاذ ورتبته", "أ.د. عبد الحميد مهري", "الأستاذ المرجعي"),
                FormFieldConfig("target_opportunity", "البرنامج أو المنحة المستهدفة", "الترشح لبرنامج ماستر التميز / مسابقة الدكتوراه", "البرنامج الأكاديمي"),
                FormFieldConfig("student_performance", "المواد التي درّسكم إياها والمعدل", "مقياس تحليل البيانات (علامة: 17.50/20 وتصنيف الأول في الفوج)", "إنجازكم في مادته")
            ),
            generateContent = { user, extra ->
                """
السلام عليكم ورحمة الله وبركاته،
الأستاذ الدكتور الفاضل: ${extra["prof_title"] ?: "أستاذنا الفاضل"}،

أتمنى أن تكونوا بأفضل حال وعافية.

أنا تلميذكم الطالب(ة): ${user.fullName}، خريج دفعة ${user.academicLevel} في ${user.specialty} بجامعة ${user.university}.

أتوجه إلى سيادتكم الكريمة بطلبي هذا ملتمساً منكم التفضل بمنحي رسالة توصية أكاديمية (Letter of Recommendation) لدعم ملف ترشحي لـ: "${extra["target_opportunity"] ?: "البرنامج الأكاديمي المنشود"}".

لقد حظيت بشرف التتلمذ على أيديكم في مقياس "${extra["student_performance"] ?: "المادة المقررة"}"، وكانت توجيهاتكم وملاحظاتكم ملهمة لمساري العلمي وطموحي البحثي.

تجدون مرفقاً مع هذه الرسالة سيرتي الذاتية وكشف النقاط وموجزاً عن البرنامج المستهدف لتسهيل تحرير الرسالة في حال موافقتكم.

تقبلوا مني خالص آيات العرفان والتقدير.

تلميذكم: ${user.fullName}
البريد: ${user.email} | الهاتف: ${user.phone}
                """.trimIndent()
            }
        ),

        // ==================== 4. THESIS, MASTER & CAREER ====================
        AdminDocumentTemplate(
            id = "lettre_motivation_fr",
            title = "Lettre de Motivation (Master / Emploi)",
            language = "Français",
            category = "التخرج والماستر",
            tag = "Forme Internationale",
            description = "Lettre de motivation académique et professionnelle rédigée selon les normes universitaires",
            requiredFields = listOf(
                FormFieldConfig("target_program", "Programme ou Poste visé", "Master 2 en Intelligence Artificielle et Big Data", "Ex: Doctorat LMD, Stage PFE..."),
                FormFieldConfig("institution_name", "Établissement / Faculté", "Faculté des Sciences et Technologies", "Nom de l'organisme"),
                FormFieldConfig("strengths", "Points forts et compétences", "Rigueur méthodologique, programmation Python/Java et esprit d'analyse", "Compétences clés")
            ),
            generateContent = { user, extra ->
                """
${user.fullName}
${user.academicLevel} - ${user.specialty}
${user.university}
Tél: ${user.phone} - Email: ${user.email}

À l'attention de la Commission de Sélection
${extra["institution_name"] ?: "Établissement Universitaire"}

Objet: Candidature pour intégrer le ${extra["target_program"] ?: "Programme de Master"}

Madame, Monsieur les membres du comité de sélection,

Titulaire d'un parcours universitaire assidu en ${user.specialty} au sein de ${user.university}, je me permets de vous soumettre ma candidature afin d'intégrer le ${extra["target_program"] ?: "programme visé"}.

Tout au long de mon cursus, j'ai développé de solides compétences théoriques et pratiques, particulièrement en ${extra["strengths"] ?: "analyse, recherche et travail d'équipe"}. Mon projet académique s'articule autour de l'excellence et de la contribution active aux projets de recherche appliquée.

Intégrer votre prestigieuse formation représente pour moi une opportunité majeure d'approfondir mes connaissances et de concrétiser mes ambitions scientifiques.

Je me tiens à votre entière disposition pour tout entretien d'évaluation ou complément d'information.

En vous remerciant pour l'attention portée à mon dossier, je vous prie d'agréer, Madame, Monsieur, l'expression de ma très haute considération.

${user.fullName}
                """.trimIndent()
            }
        ),
        AdminDocumentTemplate(
            id = "motivation_letter_ar",
            title = "رسالة تحفيزية للترشح لمسابقة الماستر أو الدكتوراه",
            language = "عربي",
            category = "التخرج والماستر",
            tag = "ماستر ودكتوراه",
            description = "صيغة رسالة تحفيز باللغة العربية للترشح للدراسات العليا وإبراز الجدارة الأكاديمية",
            requiredFields = listOf(
                FormFieldConfig("program_title", "اسم مسار الماستر أو التخصص المطلوب", "ماستر أكاديمي في هندسة البرمجيات والأنظمة الذكية", "التخصص المستهدف"),
                FormFieldConfig("academic_background", "خلفيتك الأكاديمية ونقاط التميز", "تحصيل ليسانس بمعدل ممتاز مع مرتبة الشرف وإنجاز مشروع تخرج تطبيقي", "نقاط القوة"),
                FormFieldConfig("future_vision", "المشروع المهني والأكاديمي المستقبلي", "مواصلة البحث العلمي وإعداد أطروحة دكتوراه والمساهمة في تطوير الحلول الوطنية", "الرؤية المستقبلية")
            ),
            generateContent = { user, extra ->
                """
الجمهورية الجزائرية الديمقراطية الشعبية
وزارة التعليم العالي والبحث العلمي

إلى السادة: رئيس وأعضاء لجنة الترتيب والتوجيه لمرحلة الماستر / الدكتوراه
كلية: ${user.faculty} - جامعة: ${user.university}

الموضوع: رسالة تحفيزية (Lettre de Motivation) للترشح لـ: ${extra["program_title"] ?: "التخصص المذكور"}

يشرفني أن أرفع إلى لجنتكم الموقرة ملف ترشحي لمتابعة مساري في طور: "${extra["program_title"] ?: "البرنامج الأكاديمي"}".

لقد تكللت مسيرتي الجامعية في مرحلة ${user.academicLevel} تخصص ${user.specialty} بـ: ${extra["academic_background"] ?: "الجدية والمواظبة وتحقيق نتائج متميزة وتطوير مهارات منهجية معمقة"}.

إن رغبتي الأكيدة في الالتحاق بهذا التخصص تنبع من تماشيه التام مع مشروعي العلمي والمهني الرامي إلى: ${extra["future_vision"] ?: "تعميق المعارف والمساهمة في البحوث المبتكرة"}.

أرجو أن ينال ملفي ثقتكم الغالية، وتقبلوا منا أسمى عبارات التقدير والاحترام.

المترشح(ة): ${user.fullName}
رقم الهاتف: ${user.phone}
البريد الإلكتروني: ${user.email}
                """.trimIndent()
            }
        ),
        AdminDocumentTemplate(
            id = "student_cv_ar",
            title = "السيرة الذاتية الأكاديمية للطالب (CV Étudiant)",
            language = "عربي",
            category = "التخرج والماستر",
            tag = "CV مهني",
            description = "سيرة ذاتية متوازنة تبرز المسار الجامعي والمشاريع والمهارات واللغات",
            requiredFields = listOf(
                FormFieldConfig("bac_info", "شهادة البكالوريا", "شعبة علوم تجريبية - معدل 15.42 (دورة 2021)", "الشعبة والمعدل والسنة"),
                FormFieldConfig("skills_tech", "المهارات التقنية والبرمجية", "Excel متقدم، SPSS، البرمجة بلغة Python، إدارة قواعد البيانات", "البرامج والأدوات"),
                FormFieldConfig("languages", "اللغات المتقنة", "العربية (اللغة الأم)، الفرنسية (C1)، الإنجليزية (B2)", "اللغات والمستوى"),
                FormFieldConfig("interests", "الاهتمامات والأنشطة", "عضو في النادي العلمي للجامعة، العمل التطوعي، البحث الأكاديمي", "النوادي والأنشطة")
            ),
            generateContent = { user, extra ->
                """
==================================================
              السيرة الذاتية الأكاديمية
==================================================

1. البيانات الشخصية:
• الاسم واللقب: ${user.fullName}
• الجامعة: ${user.university}
• التخصص الحالي: ${user.specialty} (${user.academicLevel})
• البريد الإلكتروني: ${user.email}
• رقم الهاتف: ${user.phone}

2. المسار الدراسي والشهادات:
• ${user.academicLevel} - تخصص ${user.specialty} (${user.university}) - [2024 - 2026]
• ليسانس في ${user.specialty} (${user.faculty}) - [2021 - 2024]
• شهادة البكالوريا: ${extra["bac_info"] ?: "معدل جيد"}

3. المشاريع الأكاديمية والبحوث:
• إعداد مذكرة تخرج بعنوان: "دراسة تطبيقية ميدانية في قطاع [المجال]"
• إنجاز تقارير ودراسات حالة متعددة في إطار مشاريع المواد الموجهة (TD/TP).

4. المهارات والقدرات:
• المهارات التقنية: ${extra["skills_tech"] ?: "الحزم المكتبية، البرمجيات الإحصائية"}
• اللغات: ${extra["languages"] ?: "العربية، الفرنسية، الإنجليزية"}
• المهارات الشخصية: العمل الجماعي، الإلقاء والعرض الأكاديمي، حل المشكلات والالتزام بالمواعيد.

5. الأنشطة الجامعية والجمعوية:
• ${extra["interests"] ?: "المشاركة في الملتقيات العلمية والنوادي الطلابية"}
==================================================
                """.trimIndent()
            }
        ),

        // ==================== 5. UNIVERSITY SERVICES & SOCIAL ====================
        AdminDocumentTemplate(
            id = "dou_housing_ar",
            title = "طلب الاستفادة من الإيواء بالإقامة الجامعية (DOU)",
            language = "عربي",
            category = "الخدمات الجامعية",
            tag = "إقامة جامعية",
            description = "طلب موجه لمدير الخدمات الجامعية للاستفادة من غرفة فردية أو تجديد الإيواء",
            requiredFields = listOf(
                FormFieldConfig("residence_name", "اسم الإقامة الجامعية", "الإقامة الجامعية للبنات / الذكور (19 ماي)", "اسم الإقامة"),
                FormFieldConfig("distance_km", "المسافة بين مقر السكن والجامعة (كم)", "أكثر من 120 كلم (ولاية أخرى)", "المسافة الكيلومترية"),
                FormFieldConfig("social_condition", "الحالة والظروف المبررة", "البعد الجغرافي وعدم توفر وسائل نقل يومية للمواظبة", "ظروف الإقامة")
            ),
            generateContent = { user, extra ->
                """
الجمهورية الجزائرية الديمقراطية الشعبية
وزارة التعليم العالي والبحث العلمي
الديوان الوطني للخدمات الجامعية (ONOU)
مديرية الخدمات الجامعية: [اسم المديرية الولائية]
الإقامة الجامعية: ${extra["residence_name"] ?: "المعنية"}

إلى السيد: مدير الإقامة الجامعية
الموضوع: طلب الاستفادة / تجديد الإيواء بالإقامة الجامعية

يشرفني أن أتقدم إلى سيادتكم المحترمة بطلبي هذا قصد الاستفادة من غرفة بالإقامة الجامعية للموسم الجامعي الحالي.

معلومات الطالب(ة):
- الاسم واللقب: ${user.fullName}
- المستوى والتخصص: ${user.academicLevel} - ${user.specialty}
- الجامعة المسجل بها: ${user.university}
- رقم بطاقة الطالب: ${user.studentIdNumber}
- العنوان الدائم: [عنوان السكن الأصلي بالولاية]
- المسافة التقديرية عن الجامعة: ${extra["distance_km"] ?: "أكثر من 50 كلم"}

المبررات:
${extra["social_condition"] ?: "نظراً لبعد مقر سكني وصعوبة التنقل اليومي لمتابعة المحاضرات والتربصات."}

تجدون رفقة الطلب استمارة التسجيل الإلكتروني وشهادة التسجيل البيداغوجي.

تقبلوا فائق عبارات التقدير والاحترام.

إمضاء الطالب(ة): ${user.fullName}
                """.trimIndent()
            }
        ),
        AdminDocumentTemplate(
            id = "honour_declaration_ar",
            title = "تصريح شرفي بعدم ممارسة أي عمل مأجور (للمنحة والإيواء)",
            language = "عربي",
            category = "الخدمات الجامعية",
            tag = "تصريح شرفي",
            description = "وثيقة رسمية مصرحة بالشرف لاستكمال ملف المنحة الجامعية أو السكن",
            requiredFields = listOf(
                FormFieldConfig("birth_place_date", "تاريخ ومكان الازدياد", "15 جانفي 2002 بـ الجزائر العاصمة", "الميلاد والمكان"),
                FormFieldConfig("id_card_num", "رقم بطاقة التعريف الوطنية", "11223344556677", "رقم بطاقة التعريف البيومترية"),
                FormFieldConfig("id_card_issued", "تاريخ وجهة الإصدار", "صادرة بتاريخ 12/05/2021 عن دائرة سيدي امحمد", "جهة وتاريخ الإصدار")
            ),
            generateContent = { user, extra ->
                """
الجمهورية الجزائرية الديمقراطية الشعبية

تصريح شرفي بعدم ممارسة أي نشاط مأجور
(لاستكمال الملف الإداري للمنحة / الإيواء الجامعي)

أنا الموقع أسفله:
- الاسم واللقب: ${user.fullName}
- المولود(ة) بتاريخ: ${extra["birth_place_date"] ?: "المحدد"}
- الحامل(ة) لبطاقة التعريف الوطنية رقم: ${extra["id_card_num"] ?: "رقم البطاقة"}
- الصادرة بتاريخ: ${extra["id_card_issued"] ?: "جهة الإصدار"}
- الطالب(ة) بـ: ${user.university} (${user.academicLevel} - ${user.specialty})
- رقم التسجيل: ${user.studentIdNumber}

أصرح بشرفي التام وبكامل قواي العقلية والقانونية أنني:
لا أمارس أي عمل مأجور أو وظيفة براتب سواء في القطاع العام أو القطاع الخاص، ولا أتقاضى أي أجر أو معاش تقاعدي خلال كامل فترة دراستي الجامعية.

كما أتحمل كامل المسؤولية القانونية والجزائية المترتبة عن أي تصريح كاذب أو مغلوط طبقاً لأحكام قانون العقوبات والتشريع المعمول به.

حرر بـ: [مكان التحرير] في: [التاريخ]
إمضاء المصرح(ة) بشرفه:
${user.fullName}
                """.trimIndent()
            }
        )
    )
}
