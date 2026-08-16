package com.example.data.powerpoint

object PowerPointCatalog {

    val allDecks: List<PowerPointDeckModel> = listOf(
        // =========================================================================
        // 1. MYSTERY BOMB GAME: PAST SIMPLE IRREGULAR VERBS
        // =========================================================================
        PowerPointDeckModel(
            id = "ppt_bomb_game_past_simple",
            title = "Mystery Bomb Game: Past Simple Irregular Verbs",
            titleTh = "เกมเปิดกล่องกู้ระเบิด: กริยา 3 ช่อง Past Simple",
            category = PptCategory.GAMES,
            gradeLevel = "M.1-M.3",
            difficulty = "Intermediate",
            totalSlides = 6,
            estimatedMinutes = 20,
            badgeIcon = "💣",
            sourceAttribution = "iSLCollective Community ESL (Interactive PPT Game)",
            description = "High-energy classroom game featuring interactive mystery boxes, bomb traps (-50 pts), gold chests (+200 pts), and irregular verb past tense transformations.",
            tags = listOf("Bomb Game", "Past Simple", "Irregular Verbs", "Team Battle", "Mystery Box"),
            slides = listOf(
                PowerPointSlide(
                    slideNumber = 1,
                    layoutType = SlideLayoutType.TITLE_HERO,
                    title = "THE MYSTERY BOMB CHALLENGE!",
                    subtitle = "Past Simple Irregular Verbs Battle · การแข่งขันกริยาช่อง 2",
                    headline = "Team Red 🔴 vs Team Blue 🔵",
                    bodyEn = "Welcome players! Choose a mystery box, transform the verb into Past Simple correctly, and earn points for your team. Watch out for hidden bombs!",
                    bodyTh = "ยินดีต้อนรับนักเรียนทุกคน! เลือกกล่องปริศนา เปลี่ยนคำกริยาเป็น Past Simple ให้ถูกต้องเพื่อรับคะแนน ระวังกล่องระเบิดซ่อนอยู่!",
                    teacherNotesEn = "Divide the class into Team Red and Team Blue. Flip a coin to determine who picks the first box.",
                    teacherNotesTh = "แบ่งนักเรียนเป็น 2 ทีม (ทีมแดง vs ทีมน้ำเงิน) สลับกันเลือกกล่องและตอบคำถาม",
                    visualEmoji = "💣"
                ),
                PowerPointSlide(
                    slideNumber = 2,
                    layoutType = SlideLayoutType.MYSTERY_BOX,
                    title = "ROUND 1: CHOOSE A MYSTERY BOX!",
                    subtitle = "Tap a box to open on screen · แตะกล่องเพื่อเปิดคำถาม",
                    teacherNotesEn = "Ask the team representative to shout the box number (1 to 6).",
                    teacherNotesTh = "ให้ตัวแทนทีมเลือกหมายเลขกล่อง แล้วกดเปิดดูคำถามพร้อมกัน",
                    visualEmoji = "🎁",
                    mysteryBoxes = listOf(
                        MysteryBoxItem(1, "What is the past tense of 'BUY'?", "รูปอดีตของ 'buy' (ซื้อ) คืออะไร?", "points", 100, "BOUGHT (ซื้อแล้ว)"),
                        MysteryBoxItem(2, "BOOM! You opened a hidden bomb trap!", "ตูม! เจอกล่องระเบิด เสีย 50 คะแนน!", "bomb", -50, "💣 -50 Points Penalty!"),
                        MysteryBoxItem(3, "What is the past tense of 'CATCH'?", "รูปอดีตของ 'catch' (จับ) คืออะไร?", "points", 150, "CAUGHT (จับแล้ว)"),
                        MysteryBoxItem(4, "SUPER GOLD CHEST! Free Bonus Points!", "หีบสมบัติทองคำ! รับคะแนนฟรีทันที!", "star", 200, "💎 +200 FREE BONUS POINTS!"),
                        MysteryBoxItem(5, "What is the past tense of 'FLY'?", "รูปอดีตของ 'fly' (บิน) คืออะไร?", "points", 100, "FLEW (บินแล้ว)"),
                        MysteryBoxItem(6, "What is the past tense of 'TEACH'?", "รูปอดีตของ 'teach' (สอน) คืออะไร?", "points", 150, "TAUGHT (สอนแล้ว)")
                    )
                ),
                PowerPointSlide(
                    slideNumber = 3,
                    layoutType = SlideLayoutType.JEOPARDY_MCQ,
                    title = "CHALLENGE SLIDE: SENTENCE PUZZLE",
                    subtitle = "150 Points Question · คำถามชิง 150 คะแนน",
                    headline = "Fill in the blank with the correct Past Simple verb:",
                    bodyEn = "Last night, Fah _____ a scary noise in the garden and called her brother.",
                    bodyTh = "เมื่อคืนนี้ ฟ้า...เสียงน่ากลัวในสวน และโทรหาพี่ชายของเธอ",
                    options = listOf("A) heard (ได้ยิน)", "B) heared", "C) hearing", "D) hears"),
                    correctAnswer = "A) heard",
                    explanationEn = "'Hear' is an irregular verb. Its past simple form is 'heard' (pronounced /hɜːrd/).",
                    explanationTh = "คำว่า 'hear' เป็น irregular verb ช่อง 2 คือ 'heard' (ออกเสียงว่า เฮิร์ด)",
                    pointsValue = 150,
                    teacherNotesEn = "Start the 30-second timer and have the team agree on a final answer before tapping Reveal.",
                    teacherNotesTh = "จับเวลา 30 วินาที ให้ทีมปรึกษากันก่อนตอบแล้วกดปุ่ม Reveal Answer",
                    visualEmoji = "👂"
                ),
                PowerPointSlide(
                    slideNumber = 4,
                    layoutType = SlideLayoutType.MYSTERY_BOX,
                    title = "ROUND 2: THE FINAL VAULT!",
                    subtitle = "High stakes round! · รอบตัดสินคะแนนพิเศษ",
                    teacherNotesEn = "Teams with lower points get first pick in this decisive round.",
                    teacherNotesTh = "ให้ทีมที่มีคะแนนตามหลังได้สิทธิ์เลือกกล่องก่อน",
                    visualEmoji = "🏆",
                    mysteryBoxes = listOf(
                        MysteryBoxItem(1, "What is the past tense of 'WRITE'?", "รูปอดีตของ 'write' คืออะไร?", "points", 100, "WROTE (เขียนแล้ว)"),
                        MysteryBoxItem(2, "What is the past tense of 'DRINK'?", "รูปอดีตของ 'drink' คืออะไร?", "points", 100, "DRANK (ดื่มแล้ว)"),
                        MysteryBoxItem(3, "DOUBLE POINTS! Answer: Past of 'CHOOSE'?", "คะแนนคูณสอง! รูปอดีตของ 'choose' คือ?", "double", 300, "CHOSE (เลือกแล้ว) -> 300 PTS!"),
                        MysteryBoxItem(4, "BOOM! Bomb in the box!", "ระเบิดระเบิดทำงาน! เสีย 50 แต้ม!", "bomb", -50, "💣 -50 Points!"),
                        MysteryBoxItem(5, "What is the past tense of 'SWIM'?", "รูปอดีตของ 'swim' คืออะไร?", "points", 100, "SWAM (ว่ายน้ำแล้ว)"),
                        MysteryBoxItem(6, "What is the past tense of 'SPEAK'?", "รูปอดีตของ 'speak' คืออะไร?", "points", 150, "SPOKE (พูดแล้ว)")
                    )
                ),
                PowerPointSlide(
                    slideNumber = 5,
                    layoutType = SlideLayoutType.SPOT_MISTAKE,
                    title = "BOSS BATTLE: SPOT THE GRAMMAR MISTAKE",
                    subtitle = "Find the error in this sentence · หาจุดผิดในประโยคนี้",
                    bodyEn = "Yesterday morning, we didn't went to school because of heavy flooding in Bangkok.",
                    bodyTh = "เมื่อเช้าวานนี้ พวกเราไม่ได้ไปโรงเรียนเพราะน้ำท่วมหนักในกรุงเทพฯ",
                    correctAnswer = "Error: 'didn't went' -> MUST BE 'didn't go'",
                    explanationEn = "Rule: After negative auxiliary 'didn't', the main verb must return to its base infinitive form (V.1), so 'didn't go' is correct.",
                    explanationTh = "กฎ: หลัง didn't กริยาต้องกลับเป็นรูปเดิมไม่ผัน (V.1) จึงต้องแก้เป็น 'didn't go'",
                    pointsValue = 200,
                    teacherNotesEn = "Explain the 'didn't + V.1' rule clearly to reinforce one of the most common O-NET mistakes.",
                    teacherNotesTh = "เน้นย้ำกฎ didn't + V.1 ให้นักเรียนจำแม่นยำ เพราะเป็นข้อสอบ O-NET ออกบ่อยมาก",
                    visualEmoji = "🕵️"
                ),
                PowerPointSlide(
                    slideNumber = 6,
                    layoutType = SlideLayoutType.SUMMARY_HOMEWORK,
                    title = "GAME OVER & LESSON WRAP-UP!",
                    subtitle = "Celebration & LINE Homework Dispatch",
                    bodyEn = "Great job today! You mastered 12 essential irregular verbs in Past Simple.",
                    bodyTh = "ยอดเยี่ยมมากทุกคน! วันนี้พวกเราได้ทบทวนกริยา 3 ช่องที่สำคัญครบ 12 คำ",
                    bulletPoints = listOf(
                        "buy -> bought (ซื้อ)",
                        "catch -> caught (จับ)",
                        "fly -> flew (บิน)",
                        "teach -> taught (สอน)",
                        "write -> wrote (เขียน)",
                        "drink -> drank (ดื่ม)",
                        "choose -> chose (เลือก)",
                        "swim -> swam (ว่ายน้ำ)"
                    ),
                    teacherNotesEn = "Tap 'Assign to LINE' to send this 12-verb study guide directly to students' inboxes.",
                    teacherNotesTh = "กดปุ่ม 'ส่งการบ้านไปยัง LINE' เพื่อส่งสรุปคำศัพท์ให้เด็กๆ ทบทวนที่บ้านได้ทันที",
                    visualEmoji = "🎉"
                )
            )
        ),

        // =========================================================================
        // 2. JEOPARDY ESL SHOWDOWN: GRAMMAR & TENSES
        // =========================================================================
        PowerPointDeckModel(
            id = "ppt_jeopardy_grammar_showdown",
            title = "Jeopardy ESL Showdown: Grammar & Tenses",
            titleTh = "เกมตอบคำถามชิงคะแนน Jeopardy: ไวยากรณ์และ Tenses",
            category = PptCategory.GAMES,
            gradeLevel = "M.1-M.6",
            difficulty = "Challenging",
            totalSlides = 7,
            estimatedMinutes = 25,
            badgeIcon = "🏆",
            sourceAttribution = "iSLCollective ESL PowerPoints",
            description = "Interactive Jeopardy-style classroom game with categories for Present Simple, Past Continuous, Prepositions, and Conditional sentences with team scoreboard.",
            tags = listOf("Jeopardy", "Grammar Battle", "Conditionals", "Prepositions", "Scoreboard"),
            slides = listOf(
                PowerPointSlide(
                    slideNumber = 1,
                    layoutType = SlideLayoutType.TITLE_HERO,
                    title = "JEOPARDY ESL SHOWDOWN 🏆",
                    subtitle = "Grammar Masters Edition · ชิงแชมป์ไวยากรณ์ภาษาอังกฤษ",
                    headline = "Categories: Tenses | Prepositions | Conditionals | Modals",
                    bodyEn = "Test your team's English mastery! Pick point values from 100 to 500 XP. Highest score takes the classroom trophy!",
                    bodyTh = "ทดสอบความรู้ภาษาอังกฤษแบบทีม! เลือกคำถามมูลค่า 100 ถึง 500 คะแนน ทีมที่ได้คะแนนสูงสุดคือผู้ชนะ!",
                    teacherNotesEn = "Assign Team Captains and test the buzzer/speaker before starting.",
                    teacherNotesTh = "ตั้งกัปตันทีม 2 ฝั่ง และอธิบายกติกาการกดกริ่งตอบ",
                    visualEmoji = "🏆"
                ),
                PowerPointSlide(
                    slideNumber = 2,
                    layoutType = SlideLayoutType.JEOPARDY_MCQ,
                    title = "CATEGORY: TENSES (100 PTS)",
                    subtitle = "Present Continuous vs Present Simple",
                    bodyEn = "Look at the window! It _____ heavily right now, so take an umbrella.",
                    bodyTh = "ดูที่หน้าต่างสิ! ตอนนี้ฝนกำลังตกหนักมาก เอาร่มไปด้วยนะ",
                    options = listOf("A) is raining", "B) rains", "C) was rain", "D) rained"),
                    correctAnswer = "A) is raining",
                    explanationEn = "'Right now' and 'Look!' are time signal keywords for Present Continuous (is/am/are + V-ing).",
                    explanationTh = "มีคำบอกเวลา 'right now' และคำอุทาน 'Look!' จึงต้องใช้ Present Continuous (is raining)",
                    pointsValue = 100,
                    teacherNotesEn = "Ask students to point out the keyword that gave away the answer.",
                    teacherNotesTh = "ถามนักเรียนว่าสังเกตจากคำบอกเวลาคำไหน (Look! / right now)",
                    visualEmoji = "🌧️"
                ),
                PowerPointSlide(
                    slideNumber = 3,
                    layoutType = SlideLayoutType.JEOPARDY_MCQ,
                    title = "CATEGORY: PREPOSITIONS (200 PTS)",
                    subtitle = "Time & Place Prepositions (In / On / At)",
                    bodyEn = "Our final English exam will take place _____ Monday morning _____ 9:00 AM.",
                    bodyTh = "การสอบปลายภาคภาษาอังกฤษจะจัดขึ้น...เช้าวันจันทร์ เวลา...9:00 น.",
                    options = listOf("A) on / at", "B) in / at", "C) at / on", "D) on / in"),
                    correctAnswer = "A) on / at",
                    explanationEn = "We use 'ON' for days (on Monday morning) and 'AT' for exact clock times (at 9:00 AM).",
                    explanationTh = "ใช้ 'ON' กับวัน (on Monday morning) และใช้ 'AT' กับเวลาบอกโมง (at 9:00 AM)",
                    pointsValue = 200,
                    teacherNotesEn = "Remind students of the triangle rule: AT (precise) -> ON (days/streets) -> IN (months/years/cities).",
                    teacherNotesTh = "ทบทวนปิรามิด In-On-At ให้นักเรียนจำสูตรลัดได้ง่าย",
                    visualEmoji = "⏰"
                ),
                PowerPointSlide(
                    slideNumber = 4,
                    layoutType = SlideLayoutType.JEOPARDY_MCQ,
                    title = "CATEGORY: CONDITIONALS (300 PTS)",
                    subtitle = "First Conditional (Real Possibility in Future)",
                    bodyEn = "If you _____ hard for the upcoming TGAT exam, you _____ into your dream university.",
                    bodyTh = "ถ้าเธอตั้งใจอ่านหนังสือสอบ TGAT เธอจะสอบติดมหาวิทยาลัยในฝันได้อย่างแน่นอน",
                    options = listOf("A) study / will get", "B) studied / would get", "C) will study / get", "D) studies / got"),
                    correctAnswer = "A) study / will get",
                    explanationEn = "First Conditional Formula: If + Present Simple (study), Future Simple (will + get).",
                    explanationTh = "สูตร First Conditional: If + Present Simple, will + V.1",
                    pointsValue = 300,
                    teacherNotesEn = "Have the whole class chant the formula together: 'If Present, will V.1!'",
                    teacherNotesTh = "ให้นักเรียนท่องสูตร 'If Present, will V.1!' พร้อมกันทั้งห้อง",
                    visualEmoji = "🎓"
                ),
                PowerPointSlide(
                    slideNumber = 5,
                    layoutType = SlideLayoutType.JEOPARDY_MCQ,
                    title = "CATEGORY: MODALS & POLITE SPEECH (400 PTS)",
                    subtitle = "Making polite requests and suggestions",
                    bodyEn = "Customer: '_____ you please show me the way to the BTS ticket counter?'\nOfficer: 'Certainly! Follow me.'",
                    bodyTh = "ลูกค้า: รบกวนช่วยกรุณาบอกทางไปเคาน์เตอร์บัตร BTS ได้ไหมครับ?\nเจ้าหน้าที่: ได้แน่นอนครับ! ตามผมมาเลย",
                    options = listOf("A) Could", "B) Must", "C) Should", "D) May not"),
                    correctAnswer = "A) Could",
                    explanationEn = "'Could you please...?' is the standard courteous polite request format in English.",
                    explanationTh = "'Could you please...?' เป็นรูปประโยคขอร้องอย่างสุภาพและเป็นสากล",
                    pointsValue = 400,
                    teacherNotesEn = "Highlight the difference in politeness between 'Can you' and 'Could you'.",
                    teacherNotesTh = "อธิบายว่า Could มีระดับความสุภาพเป็นทางการมากกว่า Can",
                    visualEmoji = "🚆"
                ),
                PowerPointSlide(
                    slideNumber = 6,
                    layoutType = SlideLayoutType.JEOPARDY_MCQ,
                    title = "GRAND FINAL: 500 XP MEGA QUESTION!",
                    subtitle = "Passive Voice Transformation Challenge",
                    bodyEn = "Active: 'Ajarn Somchai designed this interactive PowerPoint.'\nPassive: 'This interactive PowerPoint _____ by Ajarn Somchai.'",
                    bodyTh = "เปลี่ยนเป็นประโยค Passive Voice (ถูกกระทำ): สไลด์นี้...โดยอาจารย์สมชาย",
                    options = listOf("A) was designed", "B) is designing", "C) were designed", "D) has designed"),
                    correctAnswer = "A) was designed",
                    explanationEn = "Past Simple Passive formula: was/were + V.3 (designed). Since 'PowerPoint' is singular, use 'was designed'.",
                    explanationTh = "สูตร Past Passive: was/were + V.3 ประธานเป็นเอกพจน์จึงใช้ 'was designed'",
                    pointsValue = 500,
                    teacherNotesEn = "Great opportunity to review was vs were with singular and plural subjects.",
                    teacherNotesTh = "ทบทวนการเลือก was / were ให้ตรงกับประธานเอกพจน์/พหูพจน์",
                    visualEmoji = "👑"
                ),
                PowerPointSlide(
                    slideNumber = 7,
                    layoutType = SlideLayoutType.SUMMARY_HOMEWORK,
                    title = "FINAL SCORE & WINNER CELEBRATION! 🎊",
                    subtitle = "Classroom Champion Award",
                    bodyEn = "Congratulations to both teams for fantastic participation and grammar mastery!",
                    bodyTh = "ขอแสดงความยินดีกับทั้ง 2 ทีมที่ตั้งใจเล่นและตอบคำถามอย่างยอดเยี่ยม!",
                    bulletPoints = listOf(
                        "Present Continuous: is/am/are + V-ing (กำลังทำ)",
                        "Prepositions: ON (days) / AT (time) / IN (months/years)",
                        "First Conditional: If + V.1, will + V.1 (ถ้าทำ จะเกิดผล)",
                        "Polite Request: Could you please + V.1?",
                        "Passive Voice: was/were + V.3 (ถูกกระทำในอดีต)"
                    ),
                    teacherNotesEn = "Export summary directly to active class or copy for worksheet handout.",
                    teacherNotesTh = "บันทึกคะแนนและส่งสรุปเนื้อหาบทเรียนให้เด็กๆ ทางไลน์ห้อง",
                    visualEmoji = "🌟"
                )
            )
        ),

        // =========================================================================
        // 3. WOULD YOU RATHER? (THAI ESL WARM-UP EDITION)
        // =========================================================================
        PowerPointDeckModel(
            id = "ppt_would_you_rather_warmup",
            title = "Would You Rather? (Thai ESL Warm-Up Deck)",
            titleTh = "เกมชวนคุย Warm-Up: 'เธอจะเลือกอะไร?' ฉบับภาษาอังกฤษ",
            category = PptCategory.WARM_UP,
            gradeLevel = "P.4-M.6",
            difficulty = "Beginner",
            totalSlides = 6,
            estimatedMinutes = 15,
            badgeIcon = "💬",
            sourceAttribution = "iSLCollective Icebreakers",
            description = "Interactive classroom warm-up conversation slide deck with live class voting tally, sentence starters ('I would rather... because...'), and funny Thai culture choices.",
            tags = listOf("Would You Rather", "Warm-Up", "Speaking", "Icebreaker", "Voting"),
            slides = listOf(
                PowerPointSlide(
                    slideNumber = 1,
                    layoutType = SlideLayoutType.TITLE_HERO,
                    title = "WOULD YOU RATHER? 🍕 vs 🌶️",
                    subtitle = "ESL Speaking & Icebreaker Slide Deck · กิจกรรมฝึกพูดต้นคาบ",
                    headline = "Sentence Frame: 'I would rather [A] than [B] because...'",
                    bodyEn = "Get ready to debate and choose! There are no wrong answers, but you MUST give a clear English reason for your choice.",
                    bodyTh = "เตรียมตัวเลือกและให้เหตุผล! ไม่มีคำตอบผิดหรือถูก แต่ทุกคนต้องฝึกพูดประโยคภาษาอังกฤษบอกเหตุผล!",
                    teacherNotesEn = "Model the target sentence structure on the board before proceeding to Slide 2.",
                    teacherNotesTh = "เขียนโครงสร้างประโยค 'I would rather... because...' ให้นักเรียนดูเป็นตัวอย่าง",
                    visualEmoji = "🗣️"
                ),
                PowerPointSlide(
                    slideNumber = 2,
                    layoutType = SlideLayoutType.WOULD_YOU_RATHER,
                    title = "ROUND 1: FOOD DILEMMA",
                    subtitle = "Vote and explain your reason in English",
                    optionA = "Eat super spicy Som Tum Poo-Pla-Ra for breakfast every day (กินส้มตำปูปลาร้าเผ็ดจัดทุกเช้า)",
                    optionB = "Eat sweet mango sticky rice for dinner every day for a year (กินข้าวเหนียวมะม่วงทุกเย็นตลอด 1 ปี)",
                    bodyEn = "Target Sentence: 'I would rather choose Option A/B because it is more delicious / I love spicy food!'",
                    bodyTh = "ประโยคฝึกพูด: 'I would rather choose Option A because I love spicy Thai food!'",
                    teacherNotesEn = "Conduct a live hand raise and record votes using the voting tally buttons.",
                    teacherNotesTh = "ให้นักเรียนยกมือโหวตและสุ่มเรียก 2-3 คนลุกขึ้นพูดให้เหตุผลเป็นภาษาอังกฤษ",
                    visualEmoji = "🥭"
                ),
                PowerPointSlide(
                    slideNumber = 3,
                    layoutType = SlideLayoutType.WOULD_YOU_RATHER,
                    title = "ROUND 2: SUPERPOWERS",
                    subtitle = "Which superpower would you prefer?",
                    optionA = "Have the power to teleport anywhere in Thailand instantly without traffic (วาร์ปไปไหนก็ได้ในไทยทันทีไม่ต้องเจอรถติด)",
                    optionB = "Have the ability to speak every language in the world fluently (พูดได้ทุกภาษาในโลกอย่างคล่องแคล่ว)",
                    bodyEn = "Target Sentence: 'I would prefer to speak all languages because I want to travel worldwide and make global friends.'",
                    bodyTh = "ประโยคฝึกพูด: 'I would prefer Option B because speaking languages helps my future career.'",
                    teacherNotesEn = "Encourage students to use adjectives: convenient, fascinating, incredible, useful.",
                    teacherNotesTh = "กระตุ้นให้นักเรียนใช้คำคุณศัพท์ เช่น useful, convenient, amazing ในการอธิบาย",
                    visualEmoji = "⚡"
                ),
                PowerPointSlide(
                    slideNumber = 4,
                    layoutType = SlideLayoutType.WOULD_YOU_RATHER,
                    title = "ROUND 3: SCHOOL LIFE",
                    subtitle = "The ultimate classroom trade-off",
                    optionA = "Have zero homework forever, but school starts at 6:30 AM (ไม่มีการบ้านตลอดไป แต่ต้องมาเรียน 6:30 น.)",
                    optionB = "School starts at 10:00 AM, but you have 2 hours of English homework daily (เริ่มเรียน 10 โมงเช้า แต่มีการบ้านวันละ 2 ชม.)",
                    bodyEn = "Target Sentence: 'I would rather wake up early than do two hours of homework every single night!'",
                    bodyTh = "ประโยคฝึกพูด: 'I would rather start school at 10:00 AM because sleeping is important for my health!'",
                    teacherNotesEn = "Fun debate! Split the class into two sides of the room according to their choice.",
                    teacherNotesTh = "แบ่งนักเรียนยืน 2 ฝั่งห้องตามตัวเลือก แล้วให้ตัวแทนแต่ละฝั่งกล่าวโน้มน้าวใจ",
                    visualEmoji = "⏰"
                ),
                PowerPointSlide(
                    slideNumber = 5,
                    layoutType = SlideLayoutType.WOULD_YOU_RATHER,
                    title = "ROUND 4: VACATION DESTINATION",
                    subtitle = "Where would you take your class trip?",
                    optionA = "Camp on the cool, foggy peaks of Doi Inthanon in Chiang Mai (กางเต็นท์รับลมหนาวบนยอดดอยอินทนนท์)",
                    optionB = "Scuba dive with sea turtles in the crystal waters of Similan Islands (ดำน้ำดูเต่าทะเลที่หมู่เกาะสิมิลัน)",
                    bodyEn = "Target Sentence: 'I would rather visit Chiang Mai because I love mountain trekking and cool weather.'",
                    bodyTh = "ประโยคฝึกพูด: 'I would choose Similan because the marine life and coral reefs are breathtaking.'",
                    teacherNotesEn = "Review travel vocabulary: misty mountain, scuba diving, coral reefs, tent camping.",
                    teacherNotesTh = "ทบทวนคำศัพท์เกี่ยวกับการท่องเที่ยวและการผจญภัย",
                    visualEmoji = "🏝️"
                ),
                PowerPointSlide(
                    slideNumber = 6,
                    layoutType = SlideLayoutType.SUMMARY_HOMEWORK,
                    title = "SPEAKING REFLECTION & WRITING TASK",
                    subtitle = "Turn speaking into writing",
                    bodyEn = "Homework Task: Write 3 'Would you rather' sentences in your notebook giving 2 full reasons using 'because' and 'although'.",
                    bodyTh = "การบ้าน: เขียนประโยค 'Would you rather' จำนวน 3 ประโยคลงสมุด พร้อมอธิบายเหตุผลด้วยคำว่า because",
                    bulletPoints = listOf(
                        "Structure: I would rather [Verb 1] than [Verb 1].",
                        "Reasoning connector: ...because it makes me feel...",
                        "Contrast connector: ...although it might be difficult...",
                        "Vocabulary: convenient, thrilling, relaxing, exhausting"
                    ),
                    teacherNotesEn = "Push to student devices so they have the sentence frame template at home.",
                    teacherNotesTh = "ส่งเทมเพลตประโยคเข้า LINE ของนักเรียนเพื่อใช้ทำการบ้าน",
                    visualEmoji = "📝"
                )
            )
        ),

        // =========================================================================
        // 4. GRAMMAR DETECTIVE: SPOT THE MISTAKE (O-NET & TGAT)
        // =========================================================================
        PowerPointDeckModel(
            id = "ppt_spot_mistake_onet_tgat",
            title = "Grammar Detective: Spot the Mistake (O-NET & TGAT)",
            titleTh = "นักสืบไวยากรณ์: จับจุดผิดพิชิตข้อสอบ O-NET และ TGAT",
            category = PptCategory.ONET_TGAT,
            gradeLevel = "M.3-M.6",
            difficulty = "Challenging",
            totalSlides = 6,
            estimatedMinutes = 20,
            badgeIcon = "🕵️",
            sourceAttribution = "iSLCollective Exam Prep Decks",
            description = "Interactive error identification presentation training Thai students to spot Subject-Verb Agreement, Parallelism, and Tense consistency errors frequently tested on O-NET & TGAT.",
            tags = listOf("Error Identification", "O-NET", "TGAT", "Grammar Detective", "Exam Prep"),
            slides = listOf(
                PowerPointSlide(
                    slideNumber = 1,
                    layoutType = SlideLayoutType.TITLE_HERO,
                    title = "GRAMMAR DETECTIVE AGENCY 🔍",
                    subtitle = "O-NET & TGAT Error Identification Masterclass",
                    headline = "Mission: Identify the single grammatical crime in each sentence!",
                    bodyEn = "Every year, thousands of students lose points on simple grammar traps. Today, we crack the 5 most common exam tricks!",
                    bodyTh = "ในข้อสอบ O-NET และ TGAT มักมีกับดักไวยากรณ์ วันนี้เราจะมาฝึกจับผิดและแก้ให้ถูกต้อง 100%!",
                    teacherNotesEn = "Remind students: 'Read the entire sentence once before looking for the error.'",
                    teacherNotesTh = "แนะนำให้นักเรียนอ่านประโยคให้จบทั้งประโยคก่อนเริ่มมองหาจุดผิด",
                    visualEmoji = "🕵️"
                ),
                PowerPointSlide(
                    slideNumber = 2,
                    layoutType = SlideLayoutType.SPOT_MISTAKE,
                    title = "CASE #1: SUBJECT-VERB AGREEMENT TRAP",
                    subtitle = "Look closely at the subject noun phrase",
                    bodyEn = "The bouquet of yellow sunflowers on the teacher's desk smell wonderful.",
                    bodyTh = "ช่อดอกทานตะวันสีเหลืองบนโต๊ะครูส่งกลิ่นหอมชื่นใจ",
                    correctAnswer = "Error: 'smell' -> MUST BE 'smells'",
                    explanationEn = "The true subject of the sentence is the singular head noun 'The bouquet', NOT 'sunflowers'. Therefore, singular verb 'smells' with -s is required.",
                    explanationTh = "ประธานที่แท้จริงคือ 'The bouquet' (เอกพจน์) ไม่ใช่ sunflowers คำกริยาจึงต้องเติม s เป็น 'smells'",
                    pointsValue = 100,
                    teacherNotesEn = "Demonstrate crossing out prepositional phrases: [of yellow sunflowers] [on the teacher's desk].",
                    teacherNotesTh = "สอนเทคนิคการตัดส่วนขยายบุพบทออก เพื่อหาประธานหลักที่แท้จริง",
                    visualEmoji = "🌻"
                ),
                PowerPointSlide(
                    slideNumber = 3,
                    layoutType = SlideLayoutType.SPOT_MISTAKE,
                    title = "CASE #2: PARALLEL STRUCTURE VIOLATION",
                    subtitle = "Check lists joined by 'and', 'or', or 'but'",
                    bodyEn = "During the weekend, Praew enjoys reading novels, baking cookies, and to ride her bicycle in Chatuchak Park.",
                    bodyTh = "ในช่วงวันหยุด แพรวชอบอ่านนิยาย อบขนมคุ้กกี้ และปั่นจักรยานที่สวนจตุจักร",
                    correctAnswer = "Error: 'to ride' -> MUST BE 'riding'",
                    explanationEn = "Parallelism Rule: Items in a series must share the same grammatical form: reading (V-ing), baking (V-ing), and riding (V-ing).",
                    explanationTh = "กฎคู่ขนาน (Parallelism): เมื่อเชื่อมด้วย and รูปคำต้องเหมือนกัน: reading, baking, และ riding",
                    pointsValue = 150,
                    teacherNotesEn = "Point out how mixing infinitives and gerunds sounds unnatural.",
                    teacherNotesTh = "ชี้ให้เห็นว่าการใช้ V-ing ปนกับ to V.1 ทำให้จังหวะประโยคผิดหลักไวยากรณ์",
                    visualEmoji = "🚲"
                ),
                PowerPointSlide(
                    slideNumber = 4,
                    layoutType = SlideLayoutType.SPOT_MISTAKE,
                    title = "CASE #3: ADJECTIVE VS ADVERB CONFUSION",
                    subtitle = "Does this word modify a noun or an action verb?",
                    bodyEn = "Although the train arrived late, the tour guide explained the temple history very quick.",
                    bodyTh = "แม้ว่ารถไฟจะมาถึงล่าช้า แต่มัคคุเทศก์ก็อธิบายประวัติศาสตร์วัดได้อย่างรวดเร็วมาก",
                    correctAnswer = "Error: 'quick' -> MUST BE 'quickly'",
                    explanationEn = "'Explained' is an action verb, so it requires an adverb of manner ('quickly'), not an adjective ('quick').",
                    explanationTh = "'explained' เป็นกริยาบอกการกระทำ ต้องขยายด้วยกริยาวิเศษณ์ (Adverb) คือ 'quickly' ไม่ใช่ quick",
                    pointsValue = 150,
                    teacherNotesEn = "Have students practice converting adjectives to adverbs: quick -> quickly, careful -> carefully, fluent -> fluently.",
                    teacherNotesTh = "ให้นักเรียนฝึกเปลี่ยน Adj เป็น Adv โดยเติม -ly",
                    visualEmoji = "🛕"
                ),
                PowerPointSlide(
                    slideNumber = 5,
                    layoutType = SlideLayoutType.SPOT_MISTAKE,
                    title = "CASE #4: DOUBLE COMPARATIVE CRIME",
                    subtitle = "Can we use 'more' and '-er' together?",
                    bodyEn = "Living in Chiang Mai during winter is more cheaper than renting an apartment in downtown Bangkok.",
                    bodyTh = "การอาศัยอยู่ในเชียงใหม่ช่วงฤดูหนาวราคาถูกกว่าการเช่าอพาร์ตเมนต์ใจกลางกรุงเทพฯ",
                    correctAnswer = "Error: 'more cheaper' -> MUST BE 'cheaper' (or 'much cheaper')",
                    explanationEn = "Never combine 'more' with an '-er' comparative adjective. Use 'cheaper' alone, or 'much cheaper' for emphasis.",
                    explanationTh = "ห้ามใช้ more ซ้ำซ้อนกับคำที่เติม -er ให้ใช้ 'cheaper' หรือ 'much cheaper' หากต้องการเน้น",
                    pointsValue = 200,
                    teacherNotesEn = "Remind students: 'more better', 'more easier', 'more cheaper' are strictly forbidden in standard English.",
                    teacherNotesTh = "เน้นย้ำคำต้องห้าม: more better, more easier, more cheaper ข้อสอบชอบหลอกบ่อยมาก",
                    visualEmoji = "🏙️"
                ),
                PowerPointSlide(
                    slideNumber = 6,
                    layoutType = SlideLayoutType.SUMMARY_HOMEWORK,
                    title = "CASE CLOSED: DETECTIVE CERTIFICATE 📜",
                    subtitle = "Key Rules to Remember for O-NET & TGAT",
                    bodyEn = "Excellent detection skills! Review these 4 golden rules for your next exam.",
                    bodyTh = "ยอดเยี่ยมมาก! สรุป 4 กฎทองคำที่ต้องจำให้ขึ้นใจก่อนเข้าห้องสอบ",
                    bulletPoints = listOf(
                        "1. Find the REAL Subject (Ignore prepositional phrases like 'of...', 'in...')",
                        "2. Parallel Structure: V-ing, V-ing, and V-ing",
                        "3. Adverbs modify verbs: explain quickly, speak fluently",
                        "4. No double comparatives: use 'cheaper', never 'more cheaper'"
                    ),
                    teacherNotesEn = "One-tap dispatch to class roster or printable summary sheet.",
                    teacherNotesTh = "กดส่งชีทสรุป 4 กฎทองไปยัง LINE ของนักเรียนในห้อง",
                    visualEmoji = "🎯"
                )
            )
        ),

        // =========================================================================
        // 5. PRESENT SIMPLE VS CONTINUOUS ANIMATED STORY DECK
        // =========================================================================
        PowerPointDeckModel(
            id = "ppt_present_continuous_story",
            title = "Somchai's Day: Present Simple vs Continuous Story",
            titleTh = "เรื่องเล่าวันวุ่นๆ ของสมชาย: เปรียบเทียบ Present Simple และ Continuous",
            category = PptCategory.GRAMMAR,
            gradeLevel = "P.4-M.2",
            difficulty = "Beginner",
            totalSlides = 6,
            estimatedMinutes = 15,
            badgeIcon = "📖",
            sourceAttribution = "iSLCollective Animated Grammar Decks",
            description = "Charming visual slide deck contrasting daily routines (Somchai rides his bike every morning) with temporary actions happening right now (Somchai is fixing his flat tire).",
            tags = listOf("Present Simple", "Present Continuous", "Daily Routine", "Grammar Story", "Visual ESL"),
            slides = listOf(
                PowerPointSlide(
                    slideNumber = 1,
                    layoutType = SlideLayoutType.TITLE_HERO,
                    title = "MEET SOMCHAI! 👦",
                    subtitle = "Habits vs What's Happening Now · กิจวัตรประจำวัน vs สิ่งที่กำลังทำ",
                    headline = "Routine (Every day) vs Temporary (Right now)",
                    bodyEn = "Somchai is a 13-year-old student at Suksan School. Let's see how his English changes when talking about daily habits versus right now!",
                    bodyTh = "สมชายเป็นนักเรียนอายุ 13 ปี มาดูกันว่าภาษาอังกฤษที่ใช้บอกกิจวัตร กับสิ่งที่กำลังทำอยู่ตอนนี้ แตกต่างกันอย่างไร!",
                    teacherNotesEn = "Introduce the characters and ask students about their own morning routines.",
                    teacherNotesTh = "แนะนำตัวละครสมชาย แล้วถามนักเรียนสั้นๆ ว่าตื่นกี่โมงทุกวัน",
                    visualEmoji = "👦"
                ),
                PowerPointSlide(
                    slideNumber = 2,
                    layoutType = SlideLayoutType.GRAMMAR_RULE,
                    title = "RULE COMPARISON CHART 📊",
                    subtitle = "How to choose between the two tenses",
                    bodyEn = "Present Simple = Habits & Facts (V.1 / V-s)\nPresent Continuous = Happening NOW (is/am/are + V-ing)",
                    bodyTh = "Present Simple = นิสัย/ความจริง (ประธานเอกพจน์ กริยาเติม s)\nPresent Continuous = กำลังเกิดขึ้นตอนนี้ (is/am/are + V-ing)",
                    bulletPoints = listOf(
                        "⏰ Present Simple Keywords: always, usually, every day, often, never",
                        "⚡ Present Continuous Keywords: now, right now, at the moment, Look!, Listen!"
                    ),
                    teacherNotesEn = "Have students repeat the keywords out loud in rhythm.",
                    teacherNotesTh = "ให้นักเรียนท่องคำบอกเวลาพร้อมปรบมือเข้าจังหวะ",
                    visualEmoji = "📊"
                ),
                PowerPointSlide(
                    slideNumber = 3,
                    layoutType = SlideLayoutType.JEOPARDY_MCQ,
                    title = "SCENARIO 1: MORNING COMMUTE",
                    subtitle = "What is Somchai doing?",
                    bodyEn = "Somchai usually _____ to school by bus, but today he _____ because the weather is wonderful.",
                    bodyTh = "สมชายมักจะ...ไปโรงเรียนด้วยรถเมล์ แต่วันนี้เขา...เพราะอากาศสดใสมาก",
                    options = listOf("A) goes / is walking", "B) is going / walks", "C) go / walking", "D) went / will walk"),
                    correctAnswer = "A) goes / is walking",
                    explanationEn = "'Usually' indicates a routine habit -> 'goes'. 'Today' indicates a temporary action -> 'is walking'.",
                    explanationTh = "'usually' บอกกิจวัตรประจำ -> goes / 'today' บอกเหตุการณ์เฉพาะวันนี้ -> is walking",
                    pointsValue = 100,
                    teacherNotesEn = "Highlight the contrast created by the conjunction 'but today'.",
                    teacherNotesTh = "ชี้ให้เห็นคำเชื่อม 'but today' ที่เปลี่ยนบริบทเป็นเหตุการณ์ชั่วคราว",
                    visualEmoji = "🚌"
                ),
                PowerPointSlide(
                    slideNumber = 4,
                    layoutType = SlideLayoutType.JEOPARDY_MCQ,
                    title = "SCENARIO 2: AT THE CANTEEN",
                    subtitle = "Listen to the noise in the background!",
                    bodyEn = "Listen! The school bell _____ loudly. The students _____ delicious lunch in the canteen right now.",
                    bodyTh = "ฟังดูสิ! กริ่งโรงเรียนกำลังดัง นักเรียนกำลังรับประทานอาหารกลางวันแสนอร่อยในโรงอาหารตอนนี้",
                    options = listOf("A) is ringing / are eating", "B) rings / eat", "C) rang / ate", "D) is ring / eating"),
                    correctAnswer = "A) is ringing / are eating",
                    explanationEn = "'Listen!' and 'right now' require Present Continuous: is ringing (singular bell), are eating (plural students).",
                    explanationTh = "'Listen!' และ 'right now' บังคับใช้ is ringing (กริ่งเอกพจน์) และ are eating (นักเรียนพหูพจน์)",
                    pointsValue = 100,
                    teacherNotesEn = "Notice the subject agreement: bell -> is, students -> are.",
                    teacherNotesTh = "เน้นย้ำ bell (เอกพจน์ -> is) และ students (พหูพจน์ -> are)",
                    visualEmoji = "🔔"
                ),
                PowerPointSlide(
                    slideNumber = 5,
                    layoutType = SlideLayoutType.SPOT_MISTAKE,
                    title = "STATIVE VERBS TRAP: CAN WE USE -ING?",
                    subtitle = "Feelings, senses, and ownership verbs",
                    bodyEn = "Somchai is wanting a cold glass of Cha Yen (Thai milk tea) right now.",
                    bodyTh = "สมชายกำลังต้องการชาเย็นเย็นๆ สักแก้วตอนนี้",
                    correctAnswer = "Error: 'is wanting' -> MUST BE 'wants'",
                    explanationEn = "Stative verbs (want, love, like, know, understand, believe, have) express states, NOT physical actions, so they rarely take -ing form.",
                    explanationTh = "คำกริยาแสดงความรู้สึก/ความต้องการ (Stative Verbs เช่น want, like, love, understand) ไม่นิยมใช้รูป -ing จึงต้องใช้ 'wants'",
                    pointsValue = 150,
                    teacherNotesEn = "List key stative verbs on board: like, love, hate, want, need, know, understand.",
                    teacherNotesTh = "เขียนสรุป Stative Verbs ที่ห้ามเติม -ing บนกระดาน",
                    visualEmoji = "🧋"
                ),
                PowerPointSlide(
                    slideNumber = 6,
                    layoutType = SlideLayoutType.SUMMARY_HOMEWORK,
                    title = "LESSON RECAP & LINE CHALLENGE 📲",
                    subtitle = "Daily Habits vs Actions Happening Now",
                    bodyEn = "Well done! Send your 2 personal sentences to the class LINE group before 8:00 PM tonight.",
                    bodyTh = "เก่งมากทุกคน! แต่งประโยคบอกกิจวัตร 1 ประโยค และสิ่งที่กำลังทำตอนนี้ 1 ประโยค ส่งทางไลน์กลุ่ม",
                    bulletPoints = listOf(
                        "1. Daily habit: I usually [play football / study English] every afternoon.",
                        "2. Right now: At the moment, I am [doing homework / eating dinner]."
                    ),
                    teacherNotesEn = "Assign directly to active class roster with one tap.",
                    teacherNotesTh = "ส่งการบ้านไปยัง LINE นักเรียนพร้อมระบบตรวจเช็กอัตโนมัติ",
                    visualEmoji = "📱"
                )
            )
        ),

        // =========================================================================
        // 6. BANGKOK STREET FOOD: ORDERING & RESTAURANT ROLEPLAY PPT
        // =========================================================================
        PowerPointDeckModel(
            id = "ppt_bangkok_street_food",
            title = "Bangkok Food Safari: Ordering & Restaurant Roleplay",
            titleTh = "ตะลุยสตรีทฟู้ดกรุงเทพฯ: สไลด์ฝึกสั่งอาหารและสนทนาในร้านอาหาร",
            category = PptCategory.CULTURE_TRAVEL,
            gradeLevel = "P.4-M.6",
            difficulty = "Beginner",
            totalSlides = 6,
            estimatedMinutes = 20,
            badgeIcon = "🍲",
            sourceAttribution = "iSLCollective Communicative Roleplay Decks",
            description = "Vibrant communicative presentation featuring authentic Thai street food ordering, expressing dietary preferences (not too spicy, no sugar), asking for the bill, and roleplaying in pairs.",
            tags = listOf("Street Food", "Ordering Food", "Roleplay", "Speaking", "Thai Culture"),
            slides = listOf(
                PowerPointSlide(
                    slideNumber = 1,
                    layoutType = SlideLayoutType.TITLE_HERO,
                    title = "BANGKOK FOOD SAFARI 🍲",
                    subtitle = "Street Food Ordering & Practical English · ภาษาอังกฤษสั่งอาหารริมทาง",
                    headline = "Objective: Order your favorite dish with custom requests in English!",
                    bodyEn = "Imagine foreign tourists visiting a vibrant Bangkok night market. Learn how to recommend Thai food, explain ingredients, and take orders smoothly!",
                    bodyTh = "จำลองสถานการณ์พานักท่องเที่ยวชาวต่างชาติไปชิมสตรีทฟู้ด ฝึกแนะนำเมนู สั่งอาหาร และบอกระดับความเผ็ดเป็นภาษาอังกฤษ!",
                    teacherNotesEn = "Ask students what their favorite street food dish is in English.",
                    teacherNotesTh = "ถามนักเรียนว่าเมนูสตรีทฟู้ดจานโปรดคืออะไร ให้ออกเสียงเป็นภาษาอังกฤษ",
                    visualEmoji = "🍜"
                ),
                PowerPointSlide(
                    slideNumber = 2,
                    layoutType = SlideLayoutType.DIALOGUE_ROLEPLAY,
                    title = "USEFUL PHRASES FOR CUSTOMERS & SELLERS",
                    subtitle = "Tap audio to hear native pronunciation · กดฟังเสียงอ่านบทสนทนา",
                    dialogueLines = listOf(
                        "Vendor" to "Sawadee khrup! What would you like to order today?",
                        "Customer" to "I'd like one plate of Pad Thai with fresh prawns, please.",
                        "Vendor" to "Sure thing! How spicy would you like it?",
                        "Customer" to "Mild, please. Not too spicy, and no added sugar.",
                        "Vendor" to "Got it! That will be 60 Baht. Would you like a drink as well?",
                        "Customer" to "Yes, one iced coconut water, please. Keep the change!"
                    ),
                    teacherNotesEn = "Have students practice in pairs: Student A (Vendor) and Student B (Customer).",
                    teacherNotesTh = "ให้นักเรียนจับคู่ A/B สวมบทบาทเป็นแม่ค้าและลูกค้าฝึกพูดโต้ตอบ",
                    visualEmoji = "🥥"
                ),
                PowerPointSlide(
                    slideNumber = 3,
                    layoutType = SlideLayoutType.JEOPARDY_MCQ,
                    title = "PRACTICAL CHALLENGE: DIETARY REQUESTS",
                    subtitle = "How to say 'ไม่ใส่ผงชูรส' and 'ไม่เผ็ด' politely?",
                    bodyEn = "Tourist: 'Excuse me, could you please make my Tom Yum soup _____ because I cannot eat chili?'",
                    bodyTh = "นักท่องเที่ยว: ขอโทษนะครับ รบกวนช่วยทำต้มยำแบบ...ให้หน่อยได้ไหมครับ เพราะผมทานเผ็ดไม่ได้",
                    options = listOf("A) mild / not spicy at all", "B) very spicy and hot", "C) extra chili powder", "D) with salty ice"),
                    correctAnswer = "A) mild / not spicy at all",
                    explanationEn = "'Mild' (/maɪld/) means gently flavored and not spicy.",
                    explanationTh = "'Mild' แปลว่า เผ็ดน้อย รสชาติกลมกล่อมไม่จัดจ้าน",
                    pointsValue = 100,
                    teacherNotesEn = "Teach spice levels: non-spicy -> mild -> medium -> very spicy -> blazing hot!",
                    teacherNotesTh = "สอนระดับความเผ็ด 5 ระดับให้นักเรียนนำไปใช้ในชีวิตจริง",
                    visualEmoji = "🌶️"
                ),
                PowerPointSlide(
                    slideNumber = 4,
                    layoutType = SlideLayoutType.JEOPARDY_MCQ,
                    title = "PRACTICAL CHALLENGE: ASKING FOR THE BILL",
                    subtitle = "Polite expressions when finishing your meal",
                    bodyEn = "Customer: 'We are finished with our meal. _____ please?'\nServer: 'Right away, sir. The total is 240 Baht.'",
                    bodyTh = "ลูกค้า: พวกเราทานเสร็จแล้วครับ รบกวน...ด้วยครับ?\nพนักงาน: สักครู่นะครับ ยอดรวมทั้งหมด 240 บาทครับ",
                    options = listOf("A) Could we have the bill / check", "B) Give me all your money", "C) Where is the police", "D) Can I break the table"),
                    correctAnswer = "A) Could we have the bill / check",
                    explanationEn = "'Could we have the bill, please?' (UK) or 'Check, please?' (US) are standard polite ways to pay.",
                    explanationTh = "ใช้วลี 'Could we have the bill, please?' (แบบอังกฤษ) หรือ 'Check, please' (แบบอเมริกัน)",
                    pointsValue = 100,
                    teacherNotesEn = "Explain the difference between British 'bill' and American 'check'.",
                    teacherNotesTh = "อธิบายความแตกต่างระหว่าง Bill (อังกฤษ) และ Check (อเมริกัน)",
                    visualEmoji = "🧾"
                ),
                PowerPointSlide(
                    slideNumber = 5,
                    layoutType = SlideLayoutType.SPOT_MISTAKE,
                    title = "FOOD VOCABULARY TRAP: COUNTABLE VS UNCOUNTABLE",
                    subtitle = "Spot the grammar error in this order",
                    bodyEn = "I would like two rices, three waters, and two plates of Pad Krapow Gai, please.",
                    bodyTh = "ฉันขอข้าว 2 จาน น้ำ 3 แก้ว และผัดกะเพราไก่ 2 จานค่ะ",
                    correctAnswer = "Error: 'two rices, three waters' -> MUST BE 'two bowls of rice, three glasses/bottles of water'",
                    explanationEn = "Rice and water are uncountable mass nouns. You must use measure words (two bowls of rice, three bottles of water).",
                    explanationTh = "ข้าวและน้ำเป็นคำนามนับไม่ได้ (Uncountable) ต้องใส่หน่วยภาชนะ เช่น two bowls of rice, three bottles of water",
                    pointsValue = 150,
                    teacherNotesEn = "Review measure words: a cup of tea, a bowl of soup, a bottle of water, a plate of noodles.",
                    teacherNotesTh = "ทบทวนคำบอกลักษณะนามภาษาอังกฤษ: bowl, cup, bottle, plate",
                    visualEmoji = "🍚"
                ),
                PowerPointSlide(
                    slideNumber = 6,
                    layoutType = SlideLayoutType.SUMMARY_HOMEWORK,
                    title = "FOOD TRUCK MENU PROJECT 🎨",
                    subtitle = "Create your dream street food menu",
                    bodyEn = "Pair Project: Design a mini English menu for a Thai food stall featuring 3 dishes, prices in Baht, and spicy ratings.",
                    bodyTh = "งานกลุ่มคู่: ออกแบบเมนูอาหารสตรีทฟู้ดภาษาอังกฤษ 3 เมนู พร้อมราคา และระบุระดับความเผ็ด",
                    bulletPoints = listOf(
                        "Include dish names (e.g. Crispy Pork Basil over Rice)",
                        "Add a short English description of ingredients",
                        "Indicate price and spicy level (Mild / Medium / Hot)",
                        "Present your 1-minute pitch to the class next period"
                    ),
                    teacherNotesEn = "Push assignment to active student class with grading rubric.",
                    teacherNotesTh = "ส่งใบงานและเกณฑ์การให้คะแนนเข้าสู่ระบบนักเรียน",
                    visualEmoji = "🍛"
                )
            )
        ),

        // =========================================================================
        // 7. MODAL VERBS OF OBLIGATION: MUST, HAVE TO, SHOULD
        // =========================================================================
        PowerPointDeckModel(
            id = "ppt_school_rules_modals",
            title = "School Rules & Daily Life: Modals (Must, Have to, Should)",
            titleTh = "กฎระเบียบโรงเรียนและการใช้ชีวิต: กริยาช่วย Must, Have to, Should",
            category = PptCategory.GRAMMAR,
            gradeLevel = "M.1-M.4",
            difficulty = "Intermediate",
            totalSlides = 6,
            estimatedMinutes = 18,
            badgeIcon = "⚖️",
            sourceAttribution = "iSLCollective Grammar Rules Slides",
            description = "Clear, situational slide deck contrasting strict obligation (Must / Have to), prohibition (Must not), and friendly advice / suggestions (Should / Ought to).",
            tags = listOf("Modal Verbs", "Must", "Have to", "Should", "School Rules", "Obligation"),
            slides = listOf(
                PowerPointSlide(
                    slideNumber = 1,
                    layoutType = SlideLayoutType.TITLE_HERO,
                    title = "RULES & ADVICE: MODAL VERBS ⚖️",
                    subtitle = "Must, Have to, and Should in Action · การบอกกฎและการให้คำแนะนำ",
                    headline = "Obligation (จำเป็นต้อง) vs Advice (ควรจะ) vs Prohibition (ห้าม)",
                    bodyEn = "When should we use 'MUST', 'HAVE TO', and 'SHOULD'? Master the nuances of English obligation with real-world school scenarios!",
                    bodyTh = "เมื่อไหร่ควรใช้ Must, Have to หรือ Should? มาเรียนรู้ระดับความเข้มงวดและกฎระเบียบต่างๆ กัน!",
                    teacherNotesEn = "Write the three words on board with symbols: MUST (🔴 100% Law), SHOULD (🟡 50% Advice).",
                    teacherNotesTh = "เขียนเปรียบเทียบระดับความเข้มงวดบนกระดาน ให้นักเรียนเห็นภาพชัดเจน",
                    visualEmoji = "⚖️"
                ),
                PowerPointSlide(
                    slideNumber = 2,
                    layoutType = SlideLayoutType.GRAMMAR_RULE,
                    title = "MODAL SPECTRUM GUIDE 🚦",
                    subtitle = "Understanding strength and obligation",
                    bodyEn = "🔴 MUST / HAVE TO = Strong obligation / Rule / Law (ต้องทำตามกฎ)\n⛔ MUST NOT = Strict prohibition (ห้ามทำเด็ดขาด)\n🟡 SHOULD = Friendly advice / Recommendation (ควรทำ เป็นคำแนะนำดีๆ)",
                    bodyTh = "🔴 MUST / HAVE TO = ข้อบังคับ กฎหมาย ต้องทำ\n⛔ MUST NOT = ข้อห้าม ห้ามทำอย่างเด็ดขาด\n🟡 SHOULD = ข้อเสนอแนะ ควรจะทำ",
                    bulletPoints = listOf(
                        "Formula: Subject + Modal (Must/Should) + Base Verb (V.1 without to)",
                        "Example: You must wear your school uniform on Mondays.",
                        "Example: You should drink 8 glasses of water every day."
                    ),
                    teacherNotesEn = "Remind students: After modal verbs, NEVER add -s, -ed, or -ing to the main verb.",
                    teacherNotesTh = "ย้ำเตือน: หลัง modal verbs กริยาหลักต้องเป็น V.1 ไม่ผันเสมอ",
                    visualEmoji = "🚦"
                ),
                PowerPointSlide(
                    slideNumber = 3,
                    layoutType = SlideLayoutType.JEOPARDY_MCQ,
                    title = "SCENARIO 1: EXAM ROOM RULES",
                    subtitle = "Strict prohibition during exams",
                    bodyEn = "Notice on Examination Room Door: 'Students _____ use mobile phones or smartwatches during the test.'",
                    bodyTh = "ป้ายหน้าห้องสอบ: 'นักเรียน...ใช้โทรศัพท์มือถือหรือสมาร์ทวอทช์ในระหว่างการสอบอย่างเด็ดขาด'",
                    options = listOf("A) must not (ห้ามเด็ดขาด)", "B) should (ควรจะ)", "C) don't have to (ไม่จำเป็นต้อง)", "D) might (อาจจะ)"),
                    correctAnswer = "A) must not",
                    explanationEn = "'Must not' expresses strict prohibition (forbidden by school regulations).",
                    explanationTh = "'Must not' ใช้กับข้อห้ามทางกฎหรือระเบียบโรงเรียน (ห้ามทำเด็ดขาด)",
                    pointsValue = 100,
                    teacherNotesEn = "Ask: What is the difference between 'must not' (forbidden) and 'don't have to' (optional)?",
                    teacherNotesTh = "เน้นความแตกต่างระหว่าง must not (ห้าม) กับ don't have to (ไม่จำเป็นต้องทำ แต่ทำก็ได้)",
                    visualEmoji = "📵"
                ),
                PowerPointSlide(
                    slideNumber = 4,
                    layoutType = SlideLayoutType.JEOPARDY_MCQ,
                    title = "SCENARIO 2: SICK FRIEND ADVICE",
                    subtitle = "Giving caring recommendations",
                    bodyEn = "Friend: 'I have a high fever and a terrible sore throat.'\nYou: 'You _____ see a doctor and rest at home.'",
                    bodyTh = "เพื่อน: ฉันมีไข้สูงและเจ็บคอมากเลย\nคุณ: เธอ...ไปหาหมอและพักผ่อนที่บ้านนะ",
                    options = listOf("A) should (ควรจะ)", "B) must not (ห้าม)", "C) shouldn't (ไม่ควร)", "D) cannot (ไม่สามารถ)"),
                    correctAnswer = "A) should",
                    explanationEn = "'Should' is used to give caring personal advice and recommendations.",
                    explanationTh = "'Should' ใช้ในการให้คำแนะนำด้วยความห่วงใย",
                    pointsValue = 100,
                    teacherNotesEn = "Have students suggest alternate advice: 'You should take medicine', 'You should drink warm water'.",
                    teacherNotesTh = "ให้นักเรียนเสนอคำแนะนำอื่นๆ เพิ่มเติมเป็นภาษาอังกฤษ",
                    visualEmoji = "🩺"
                ),
                PowerPointSlide(
                    slideNumber = 5,
                    layoutType = SlideLayoutType.SPOT_MISTAKE,
                    title = "MODAL GRAMMAR TRAP: THE 'TO' INFECTION",
                    subtitle = "Find the redundant word in this sentence",
                    bodyEn = "All students must to submit their science project before Friday afternoon.",
                    bodyTh = "นักเรียนทุกคนต้องส่งโครงงานวิทยาศาสตร์ก่อนช่วงบ่ายวันศุกร์",
                    correctAnswer = "Error: 'must to submit' -> MUST BE 'must submit'",
                    explanationEn = "'Must' takes a bare infinitive directly without 'to'. (Note: 'have to submit' uses 'to', but 'must' NEVER uses 'to').",
                    explanationTh = "หลัง 'must' ต้องตามด้วยกริยาแท้รูปเดิมทันที ห้ามใส่ to (ต้องเป็น must submit ไม่ใช่ must to submit)",
                    pointsValue = 150,
                    teacherNotesEn = "Contrast: 'must submit' vs 'have to submit'.",
                    teacherNotesTh = "เปรียบเทียบ must submit (ไม่มี to) กับ have to submit (มี to)",
                    visualEmoji = "🔬"
                ),
                PowerPointSlide(
                    slideNumber = 6,
                    layoutType = SlideLayoutType.SUMMARY_HOMEWORK,
                    title = "CLASSROOM CODE OF CONDUCT POSTER 📜",
                    subtitle = "Create 3 rules using Must, Must not, and Should",
                    bodyEn = "Write 3 rules for our English classroom: 1 rule with 'must', 1 with 'must not', and 1 with 'should'.",
                    bodyTh = "เขียนกฎประจำห้องเรียนภาษาอังกฤษ 3 ข้อ (ใช้ must, must not และ should อย่างละ 1 ข้อ)",
                    bulletPoints = listOf(
                        "1. Rule: We must arrive in the classroom on time.",
                        "2. Prohibition: We must not speak rudely to classmates.",
                        "3. Advice: We should ask questions when we don't understand."
                    ),
                    teacherNotesEn = "Collect responses via class LINE inbox.",
                    teacherNotesTh = "รับการส่งผลงานผ่านระบบการบ้าน LINE ในแอป",
                    visualEmoji = "📋"
                )
            )
        ),

        // =========================================================================
        // 8. TABOO / GUESS THE SECRET WORD PPT
        // =========================================================================
        PowerPointDeckModel(
            id = "ppt_taboo_word_game",
            title = "Taboo Classroom Challenge: Guess the Secret Word",
            titleTh = "เกมใบ้คำ Taboo: ทายคำศัพท์ปริศนาห้ามพูดคำต้องห้าม",
            category = PptCategory.WARM_UP,
            gradeLevel = "P.4-M.6",
            difficulty = "Intermediate",
            totalSlides = 6,
            estimatedMinutes = 20,
            badgeIcon = "🤫",
            sourceAttribution = "iSLCollective Classroom Party Games",
            description = "Classic ESL Taboo game presentation. One student describes the secret target word to their team without uttering any of the 3 forbidden taboo words! 45-second timer included.",
            tags = listOf("Taboo", "Word Guessing", "Speaking Game", "Vocabulary", "Timer"),
            slides = listOf(
                PowerPointSlide(
                    slideNumber = 1,
                    layoutType = SlideLayoutType.TITLE_HERO,
                    title = "TABOO SECRET WORD BATTLE! 🤫",
                    subtitle = "ESL Speaking & Descriptive Fluency Challenge · เกมใบ้คำห้ามพูดคำต้องห้าม",
                    headline = "Rules: Describe the secret word without saying ANY of the 3 forbidden words!",
                    bodyEn = "One player stands facing their team. The PPT displays the target word. The player must describe it in English within 45 seconds!",
                    bodyTh = "ผู้ใบ้คำต้องอธิบายคำศัพท์ภาษาอังกฤษให้เพื่อนในทีมทายถูก โดยห้ามพูดคำต้องห้าม 3 คำที่ระบุในสไลด์ ภายในเวลา 45 วินาที!",
                    teacherNotesEn = "Call one representative from Team Red or Team Blue to face the class (screen behind them or facing screen).",
                    teacherNotesTh = "เรียกตัวแทนทีมขึ้นมาใบ้คำด้านหน้าชั้นเรียน พร้อมเปิดจับเวลา 45 วินาที",
                    visualEmoji = "🤫"
                ),
                PowerPointSlide(
                    slideNumber = 2,
                    layoutType = SlideLayoutType.TABOO_GUESS,
                    title = "SECRET WORD #1",
                    subtitle = "Team Red Turn · 45 Seconds on the Clock",
                    headline = "TARGET WORD: ELEPHANT (ช้าง)",
                    tabooForbiddenWords = listOf("Animal (สัตว์)", "Trunk (งวง)", "Big / Large (ใหญ่)"),
                    bodyEn = "Clue Tip: Describe where it lives, what it eats, Thai culture symbol, national animal, grey color, eats sugarcane/bananas!",
                    bodyTh = "คำใบ้ที่ใช้ได้: สัญลักษณ์ประจำชาติไทย, ตัวสีเทา, ชอบกินอ้อย/กล้วย, อาศัยอยู่ในป่า",
                    pointsValue = 100,
                    teacherNotesEn = "Start the timer. Buzz if the speaker uses a taboo word.",
                    teacherNotesTh = "กดเริ่มจับเวลา หากผู้ใบ้เผลอพูดคำต้องห้าม ให้กดเสียงเตือน Buzz!",
                    visualEmoji = "🐘"
                ),
                PowerPointSlide(
                    slideNumber = 3,
                    layoutType = SlideLayoutType.TABOO_GUESS,
                    title = "SECRET WORD #2",
                    subtitle = "Team Blue Turn · 45 Seconds on the Clock",
                    headline = "TARGET WORD: TUK-TUK (รถตุ๊กตุ๊ก)",
                    tabooForbiddenWords = listOf("Three wheels (สามล้อ)", "Taxi / Car (แท็กซี่/รถ)", "Bangkok (กรุงเทพฯ)"),
                    bodyEn = "Clue Tip: Famous Thai transportation, noisy engine sound, open air vehicle for tourists, blue color, fast ride!",
                    bodyTh = "คำใบ้ที่ใช้ได้: ยานพาหนะชื่อดังของไทย, มีเสียงเครื่องยนต์ดัง, ลมพัดเย็นสบาย, นักท่องเที่ยวชอบนั่ง",
                    pointsValue = 100,
                    teacherNotesEn = "Encourage expressive adjectives and hand gestures.",
                    teacherNotesTh = "ส่งเสริมให้นักเรียนใช้ท่าทางและคำคุณศัพท์ในการใบ้",
                    visualEmoji = "🛺"
                ),
                PowerPointSlide(
                    slideNumber = 4,
                    layoutType = SlideLayoutType.TABOO_GUESS,
                    title = "SECRET WORD #3",
                    subtitle = "Team Red Turn · 45 Seconds on the Clock",
                    headline = "TARGET WORD: UMBRELLA (ร่ม)",
                    tabooForbiddenWords = listOf("Rain / Water (ฝน/น้ำ)", "Sun (แดด/ดวงอาทิตย์)", "Open / Hold (เปิด/ถือ)"),
                    bodyEn = "Clue Tip: An object used in monsoon season, keeps your head and clothes dry, made of waterproof fabric, can be folded!",
                    bodyTh = "คำใบ้ที่ใช้ได้: อุปกรณ์ในฤดูมรสุม, ป้องกันเสื้อผ้าเปียก, พับเก็บใส่กระเป๋าได้",
                    pointsValue = 100,
                    teacherNotesEn = "Check team buzzer when guessed correctly.",
                    teacherNotesTh = "ให้คะแนนทีมเมื่อเพื่อนทายถูก",
                    visualEmoji = "☂️"
                ),
                PowerPointSlide(
                    slideNumber = 5,
                    layoutType = SlideLayoutType.TABOO_GUESS,
                    title = "SECRET WORD #4",
                    subtitle = "Team Blue Turn · 45 Seconds on the Clock",
                    headline = "TARGET WORD: CINEMA / MOVIE THEATER (โรงหนัง)",
                    tabooForbiddenWords = listOf("Film / Movie (หนัง/ภาพยนตร์)", "Popcorn (ป๊อปคอร์น)", "Screen / Watch (จอ/ดู)"),
                    bodyEn = "Clue Tip: A dark public hall with red comfortable seats, surround sound speakers, buy tickets at the front counter, great place for weekend dates!",
                    bodyTh = "คำใบ้ที่ใช้ได้: ห้องโถงมืดที่มีเก้าอี้สีแดงนุ่มสบาย, ลำโพงรอบทิศทาง, ซื้อตั๋วด้านหน้า",
                    pointsValue = 100,
                    teacherNotesEn = "Great way to develop circumlocution skills (explaining concepts when a word is forgotten).",
                    teacherNotesTh = "ฝึกทักษะ Circumlocution (การอธิบายความหมายโดยอ้อมเมื่อนึกคำศัพท์ไม่ออก)",
                    visualEmoji = "🍿"
                ),
                PowerPointSlide(
                    slideNumber = 6,
                    layoutType = SlideLayoutType.SUMMARY_HOMEWORK,
                    title = "TABOO MASTER CHAMPIONS! 🏆",
                    subtitle = "Fluency & Descriptive Language Accolades",
                    bodyEn = "Congratulations! Describing words without forbidden cues is one of the highest level ESL communication skills.",
                    bodyTh = "ยินดีด้วยกับทุกทีม! การอธิบายคำศัพท์โดยไม่ใช้คำต้องห้าม เป็นทักษะการสื่อสารภาษาอังกฤษขั้นสูงที่ยอดเยี่ยมมาก",
                    bulletPoints = listOf(
                        "Circumlocution: 'It is an object used for...'",
                        "Location clue: 'You can find this in a...'",
                        "Appearance clue: 'It is made of plastic/metal/wood...'",
                        "Action clue: 'People use this when they want to...'"
                    ),
                    teacherNotesEn = "Export descriptive phrase bank to student LINE inboxes.",
                    teacherNotesTh = "ส่งคลังวลีฝึกพูดอธิบายความหมายให้นักเรียนนำไปฝึกต่อที่บ้าน",
                    visualEmoji = "🎉"
                )
            )
        )
    )

    fun getDeckById(id: String): PowerPointDeckModel? {
        return allDecks.find { it.id == id }
    }

    fun getDecksByCategory(category: PptCategory): List<PowerPointDeckModel> {
        if (category == PptCategory.ALL) return allDecks
        return allDecks.filter { it.category == category }
    }

    fun searchDecks(query: String, category: PptCategory = PptCategory.ALL, grade: String? = null): List<PowerPointDeckModel> {
        val base = if (category == PptCategory.ALL) allDecks else allDecks.filter { it.category == category }
        return base.filter { deck ->
            val matchQuery = query.isBlank() || deck.title.contains(query, ignoreCase = true) ||
                    deck.titleTh.contains(query, ignoreCase = true) ||
                    deck.tags.any { it.contains(query, ignoreCase = true) } ||
                    deck.description.contains(query, ignoreCase = true)
            val matchGrade = grade == null || grade.isBlank() || grade == "All Grades" || deck.gradeLevel.contains(grade, ignoreCase = true)
            matchQuery && matchGrade
        }
    }
}
