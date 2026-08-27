-- ============================================================
-- 1. STATE
-- ============================================================

INSERT INTO states
(
    id,
    name,
    code,
    country_code,
    is_active,
    created_at,
    updated_at
)
VALUES
    (
        md5('agri-market:state:IN:KA')::uuid,
        'Karnataka',
        'KA',
        'IN',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
    ON CONFLICT (country_code, code)
DO NOTHING;


-- ============================================================
-- 2. DISTRICTS
-- ============================================================

INSERT INTO districts
(
    id,
    state_id,
    name,
    code,
    is_active,
    created_at,
    updated_at
)
VALUES

    (
        md5('agri-market:district:KA:01')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Bagalkote',
        'KA-01',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:02')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Ballari',
        'KA-02',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:03')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Belagavi',
        'KA-03',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:04')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Bengaluru Rural',
        'KA-04',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:05')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Bengaluru South',
        'KA-05',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:06')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Bengaluru Urban',
        'KA-06',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:07')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Bidar',
        'KA-07',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:08')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Chamarajanagar',
        'KA-08',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:09')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Chikkaballapura',
        'KA-09',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:10')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Chikkamagaluru',
        'KA-10',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:11')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Chitradurga',
        'KA-11',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:12')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Dakshina Kannada',
        'KA-12',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:13')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Davanagere',
        'KA-13',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:14')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Dharwad',
        'KA-14',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:15')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Gadag',
        'KA-15',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:16')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Hassan',
        'KA-16',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:17')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Haveri',
        'KA-17',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:18')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Kalaburagi',
        'KA-18',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:19')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Kodagu',
        'KA-19',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:20')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Kolar',
        'KA-20',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:21')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Koppal',
        'KA-21',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:22')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Mandya',
        'KA-22',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:23')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Mysuru',
        'KA-23',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:24')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Raichur',
        'KA-24',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:25')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Shivamogga',
        'KA-25',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:26')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Tumakuru',
        'KA-26',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:27')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Udupi',
        'KA-27',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:28')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Uttara Kannada',
        'KA-28',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:29')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Vijayapura',
        'KA-29',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:30')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Yadgir',
        'KA-30',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),

    (
        md5('agri-market:district:KA:31')::uuid,
        md5('agri-market:state:IN:KA')::uuid,
        'Vijayanagara',
        'KA-31',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )

    ON CONFLICT (state_id, code)
DO NOTHING;


-- ============================================================
-- 3. TALUKS
-- ============================================================


-- ------------------------------------------------------------
-- BAGALKOTE
-- ------------------------------------------------------------

INSERT INTO taluks
(
    id,
    district_id,
    name,
    code,
    is_active,
    created_at,
    updated_at
)
VALUES
    (
        md5('agri-market:taluk:KA-01:01')::uuid,
        md5('agri-market:district:KA:01')::uuid,
        'Bagalkote',
        'KA-01-01',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        md5('agri-market:taluk:KA-01:02')::uuid,
        md5('agri-market:district:KA:01')::uuid,
        'Jamkhandi',
        'KA-01-02',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        md5('agri-market:taluk:KA-01:03')::uuid,
        md5('agri-market:district:KA:01')::uuid,
        'Mudhola',
        'KA-01-03',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        md5('agri-market:taluk:KA-01:04')::uuid,
        md5('agri-market:district:KA:01')::uuid,
        'Badami',
        'KA-01-04',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        md5('agri-market:taluk:KA-01:05')::uuid,
        md5('agri-market:district:KA:01')::uuid,
        'Bilagi',
        'KA-01-05',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        md5('agri-market:taluk:KA-01:06')::uuid,
        md5('agri-market:district:KA:01')::uuid,
        'Hunagunda',
        'KA-01-06',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        md5('agri-market:taluk:KA-01:07')::uuid,
        md5('agri-market:district:KA:01')::uuid,
        'Ilkal',
        'KA-01-07',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        md5('agri-market:taluk:KA-01:08')::uuid,
        md5('agri-market:district:KA:01')::uuid,
        'Rabkavi Banhatti',
        'KA-01-08',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        md5('agri-market:taluk:KA-01:09')::uuid,
        md5('agri-market:district:KA:01')::uuid,
        'Guledgudda',
        'KA-01-09',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- BALLARI
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-02:01')::uuid,
                           md5('agri-market:district:KA:02')::uuid,
                           'Ballari',
                           'KA-02-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-02:02')::uuid,
                           md5('agri-market:district:KA:02')::uuid,
                           'Kurugodu',
                           'KA-02-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-02:03')::uuid,
                           md5('agri-market:district:KA:02')::uuid,
                           'Kampli',
                           'KA-02-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-02:04')::uuid,
                           md5('agri-market:district:KA:02')::uuid,
                           'Sanduru',
                           'KA-02-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-02:05')::uuid,
                           md5('agri-market:district:KA:02')::uuid,
                           'Siraguppa',
                           'KA-02-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- BELAGAVI
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-03:01')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Belagavi',
                           'KA-03-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-03:02')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Athani',
                           'KA-03-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-03:03')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Bailhongal',
                           'KA-03-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-03:04')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Chikkodi',
                           'KA-03-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-03:05')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Gokak',
                           'KA-03-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-03:06')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Khanapura',
                           'KA-03-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-03:07')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Mudalgi',
                           'KA-03-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-03:08')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Nippani',
                           'KA-03-08',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-03:09')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Rayabaga',
                           'KA-03-09',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-03:10')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Savadatti',
                           'KA-03-10',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-03:11')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Ramadurga',
                           'KA-03-11',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-03:12')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Kagawada',
                           'KA-03-12',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-03:13')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Hukkeri',
                           'KA-03-13',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-03:14')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Kitturu',
                           'KA-03-14',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-03:15')::uuid,
                           md5('agri-market:district:KA:03')::uuid,
                           'Yargatti',
                           'KA-03-15',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- BENGALURU RURAL
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-04:01')::uuid,
                           md5('agri-market:district:KA:04')::uuid,
                           'Nelamangala',
                           'KA-04-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-04:02')::uuid,
                           md5('agri-market:district:KA:04')::uuid,
                           'Doddaballapura',
                           'KA-04-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-04:03')::uuid,
                           md5('agri-market:district:KA:04')::uuid,
                           'Devanahalli',
                           'KA-04-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-04:04')::uuid,
                           md5('agri-market:district:KA:04')::uuid,
                           'Hosakote',
                           'KA-04-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- BENGALURU SOUTH
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-05:01')::uuid,
                           md5('agri-market:district:KA:05')::uuid,
                           'Channapatna',
                           'KA-05-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-05:02')::uuid,
                           md5('agri-market:district:KA:05')::uuid,
                           'Harohalli',
                           'KA-05-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-05:03')::uuid,
                           md5('agri-market:district:KA:05')::uuid,
                           'Kanakapura',
                           'KA-05-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-05:04')::uuid,
                           md5('agri-market:district:KA:05')::uuid,
                           'Magadi',
                           'KA-05-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-05:05')::uuid,
                           md5('agri-market:district:KA:05')::uuid,
                           'Ramanagara',
                           'KA-05-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- BENGALURU URBAN
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-06:01')::uuid,
                           md5('agri-market:district:KA:06')::uuid,
                           'Bengaluru',
                           'KA-06-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-06:02')::uuid,
                           md5('agri-market:district:KA:06')::uuid,
                           'Kengeri',
                           'KA-06-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-06:03')::uuid,
                           md5('agri-market:district:KA:06')::uuid,
                           'Krishnarajapura',
                           'KA-06-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-06:04')::uuid,
                           md5('agri-market:district:KA:06')::uuid,
                           'Anekal',
                           'KA-06-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-06:05')::uuid,
                           md5('agri-market:district:KA:06')::uuid,
                           'Yelahanka',
                           'KA-06-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- BIDAR
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-07:01')::uuid,
                           md5('agri-market:district:KA:07')::uuid,
                           'Aurad',
                           'KA-07-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-07:02')::uuid,
                           md5('agri-market:district:KA:07')::uuid,
                           'Basavakalyana',
                           'KA-07-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-07:03')::uuid,
                           md5('agri-market:district:KA:07')::uuid,
                           'Bhalki',
                           'KA-07-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-07:04')::uuid,
                           md5('agri-market:district:KA:07')::uuid,
                           'Bidar',
                           'KA-07-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-07:05')::uuid,
                           md5('agri-market:district:KA:07')::uuid,
                           'Chitgoppa',
                           'KA-07-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-07:06')::uuid,
                           md5('agri-market:district:KA:07')::uuid,
                           'Hulsuru',
                           'KA-07-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-07:07')::uuid,
                           md5('agri-market:district:KA:07')::uuid,
                           'Humnabad',
                           'KA-07-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-07:08')::uuid,
                           md5('agri-market:district:KA:07')::uuid,
                           'Kamalanagara',
                           'KA-07-08',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- CHAMARAJANAGAR
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-08:01')::uuid,
                           md5('agri-market:district:KA:08')::uuid,
                           'Chamarajanagar',
                           'KA-08-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-08:02')::uuid,
                           md5('agri-market:district:KA:08')::uuid,
                           'Gundlupete',
                           'KA-08-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-08:03')::uuid,
                           md5('agri-market:district:KA:08')::uuid,
                           'Kollegala',
                           'KA-08-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-08:04')::uuid,
                           md5('agri-market:district:KA:08')::uuid,
                           'Yelanduru',
                           'KA-08-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-08:05')::uuid,
                           md5('agri-market:district:KA:08')::uuid,
                           'Hanuru',
                           'KA-08-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- CHIKKABALLAPURA
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-09:01')::uuid,
                           md5('agri-market:district:KA:09')::uuid,
                           'Chikkaballapura',
                           'KA-09-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-09:02')::uuid,
                           md5('agri-market:district:KA:09')::uuid,
                           'Bagepalli',
                           'KA-09-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-09:03')::uuid,
                           md5('agri-market:district:KA:09')::uuid,
                           'Chintamani',
                           'KA-09-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-09:04')::uuid,
                           md5('agri-market:district:KA:09')::uuid,
                           'Gauribidanuru',
                           'KA-09-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-09:05')::uuid,
                           md5('agri-market:district:KA:09')::uuid,
                           'Gudibanda',
                           'KA-09-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-09:06')::uuid,
                           md5('agri-market:district:KA:09')::uuid,
                           'Sidlaghatta',
                           'KA-09-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-09:07')::uuid,
                           md5('agri-market:district:KA:09')::uuid,
                           'Cheluru',
                           'KA-09-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-09:08')::uuid,
                           md5('agri-market:district:KA:09')::uuid,
                           'Manchenahalli',
                           'KA-09-08',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- CHIKKAMAGALURU
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-10:01')::uuid,
                           md5('agri-market:district:KA:10')::uuid,
                           'Chikkamagaluru',
                           'KA-10-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-10:02')::uuid,
                           md5('agri-market:district:KA:10')::uuid,
                           'Kaduru',
                           'KA-10-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-10:03')::uuid,
                           md5('agri-market:district:KA:10')::uuid,
                           'Koppa',
                           'KA-10-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-10:04')::uuid,
                           md5('agri-market:district:KA:10')::uuid,
                           'Mudigere',
                           'KA-10-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-10:05')::uuid,
                           md5('agri-market:district:KA:10')::uuid,
                           'Narasimharajapura',
                           'KA-10-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-10:06')::uuid,
                           md5('agri-market:district:KA:10')::uuid,
                           'Sringeri',
                           'KA-10-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-10:07')::uuid,
                           md5('agri-market:district:KA:10')::uuid,
                           'Tarikere',
                           'KA-10-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-10:08')::uuid,
                           md5('agri-market:district:KA:10')::uuid,
                           'Ajjampura',
                           'KA-10-08',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-10:09')::uuid,
                           md5('agri-market:district:KA:10')::uuid,
                           'Kalasa',
                           'KA-10-09',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- CHITRADURGA
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-11:01')::uuid,
                           md5('agri-market:district:KA:11')::uuid,
                           'Chitradurga',
                           'KA-11-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-11:02')::uuid,
                           md5('agri-market:district:KA:11')::uuid,
                           'Challakere',
                           'KA-11-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-11:03')::uuid,
                           md5('agri-market:district:KA:11')::uuid,
                           'Hiriyur',
                           'KA-11-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-11:04')::uuid,
                           md5('agri-market:district:KA:11')::uuid,
                           'Holalkere',
                           'KA-11-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-11:05')::uuid,
                           md5('agri-market:district:KA:11')::uuid,
                           'Hosadurga',
                           'KA-11-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-11:06')::uuid,
                           md5('agri-market:district:KA:11')::uuid,
                           'Molakalmuru',
                           'KA-11-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- DAKSHINA KANNADA
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-12:01')::uuid,
                           md5('agri-market:district:KA:12')::uuid,
                           'Mangaluru',
                           'KA-12-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-12:02')::uuid,
                           md5('agri-market:district:KA:12')::uuid,
                           'Ullal',
                           'KA-12-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-12:03')::uuid,
                           md5('agri-market:district:KA:12')::uuid,
                           'Mulki',
                           'KA-12-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-12:04')::uuid,
                           md5('agri-market:district:KA:12')::uuid,
                           'Moodbidri',
                           'KA-12-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-12:05')::uuid,
                           md5('agri-market:district:KA:12')::uuid,
                           'Bantwala',
                           'KA-12-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-12:06')::uuid,
                           md5('agri-market:district:KA:12')::uuid,
                           'Belthangady',
                           'KA-12-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-12:07')::uuid,
                           md5('agri-market:district:KA:12')::uuid,
                           'Puttur',
                           'KA-12-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-12:08')::uuid,
                           md5('agri-market:district:KA:12')::uuid,
                           'Sulya',
                           'KA-12-08',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-12:09')::uuid,
                           md5('agri-market:district:KA:12')::uuid,
                           'Kadaba',
                           'KA-12-09',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- DAVANAGERE
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-13:01')::uuid,
                           md5('agri-market:district:KA:13')::uuid,
                           'Davanagere',
                           'KA-13-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-13:02')::uuid,
                           md5('agri-market:district:KA:13')::uuid,
                           'Harihara',
                           'KA-13-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-13:03')::uuid,
                           md5('agri-market:district:KA:13')::uuid,
                           'Channagiri',
                           'KA-13-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-13:04')::uuid,
                           md5('agri-market:district:KA:13')::uuid,
                           'Honnali',
                           'KA-13-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-13:05')::uuid,
                           md5('agri-market:district:KA:13')::uuid,
                           'Nyamathi',
                           'KA-13-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-13:06')::uuid,
                           md5('agri-market:district:KA:13')::uuid,
                           'Jagaluru',
                           'KA-13-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- DHARWAD
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-14:01')::uuid,
                           md5('agri-market:district:KA:14')::uuid,
                           'Kalghatgi',
                           'KA-14-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-14:02')::uuid,
                           md5('agri-market:district:KA:14')::uuid,
                           'Dharwad',
                           'KA-14-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-14:03')::uuid,
                           md5('agri-market:district:KA:14')::uuid,
                           'Hubballi Rural',
                           'KA-14-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-14:04')::uuid,
                           md5('agri-market:district:KA:14')::uuid,
                           'Hubballi Urban',
                           'KA-14-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-14:05')::uuid,
                           md5('agri-market:district:KA:14')::uuid,
                           'Kundagolu',
                           'KA-14-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-14:06')::uuid,
                           md5('agri-market:district:KA:14')::uuid,
                           'Navalgund',
                           'KA-14-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-14:07')::uuid,
                           md5('agri-market:district:KA:14')::uuid,
                           'Alnavara',
                           'KA-14-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-14:08')::uuid,
                           md5('agri-market:district:KA:14')::uuid,
                           'Annigeri',
                           'KA-14-08',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- GADAG
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-15:01')::uuid,
                           md5('agri-market:district:KA:15')::uuid,
                           'Gadag',
                           'KA-15-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-15:02')::uuid,
                           md5('agri-market:district:KA:15')::uuid,
                           'Nargund',
                           'KA-15-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-15:03')::uuid,
                           md5('agri-market:district:KA:15')::uuid,
                           'Mundargi',
                           'KA-15-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-15:04')::uuid,
                           md5('agri-market:district:KA:15')::uuid,
                           'Ron',
                           'KA-15-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-15:05')::uuid,
                           md5('agri-market:district:KA:15')::uuid,
                           'Gajendragad',
                           'KA-15-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-15:06')::uuid,
                           md5('agri-market:district:KA:15')::uuid,
                           'Lakshmeshwar',
                           'KA-15-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-15:07')::uuid,
                           md5('agri-market:district:KA:15')::uuid,
                           'Shirahatti',
                           'KA-15-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- HASSAN
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-16:01')::uuid,
                           md5('agri-market:district:KA:16')::uuid,
                           'Hassan',
                           'KA-16-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-16:02')::uuid,
                           md5('agri-market:district:KA:16')::uuid,
                           'Arasikere',
                           'KA-16-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-16:03')::uuid,
                           md5('agri-market:district:KA:16')::uuid,
                           'Channarayapatna',
                           'KA-16-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-16:04')::uuid,
                           md5('agri-market:district:KA:16')::uuid,
                           'Holenarasipura',
                           'KA-16-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-16:05')::uuid,
                           md5('agri-market:district:KA:16')::uuid,
                           'Sakleshpur',
                           'KA-16-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-16:06')::uuid,
                           md5('agri-market:district:KA:16')::uuid,
                           'Alur',
                           'KA-16-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-16:07')::uuid,
                           md5('agri-market:district:KA:16')::uuid,
                           'Arkalgud',
                           'KA-16-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-16:08')::uuid,
                           md5('agri-market:district:KA:16')::uuid,
                           'Belur',
                           'KA-16-08',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- HAVERI
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-17:01')::uuid,
                           md5('agri-market:district:KA:17')::uuid,
                           'Ranibennur',
                           'KA-17-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-17:02')::uuid,
                           md5('agri-market:district:KA:17')::uuid,
                           'Byadgi',
                           'KA-17-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-17:03')::uuid,
                           md5('agri-market:district:KA:17')::uuid,
                           'Hangal',
                           'KA-17-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-17:04')::uuid,
                           md5('agri-market:district:KA:17')::uuid,
                           'Haveri',
                           'KA-17-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-17:05')::uuid,
                           md5('agri-market:district:KA:17')::uuid,
                           'Savanur',
                           'KA-17-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-17:06')::uuid,
                           md5('agri-market:district:KA:17')::uuid,
                           'Hirekerur',
                           'KA-17-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-17:07')::uuid,
                           md5('agri-market:district:KA:17')::uuid,
                           'Shiggaon',
                           'KA-17-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-17:08')::uuid,
                           md5('agri-market:district:KA:17')::uuid,
                           'Rattihalli',
                           'KA-17-08',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- KALABURAGI
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-18:01')::uuid,
                           md5('agri-market:district:KA:18')::uuid,
                           'Kalaburagi',
                           'KA-18-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-18:02')::uuid,
                           md5('agri-market:district:KA:18')::uuid,
                           'Afzalpur',
                           'KA-18-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-18:03')::uuid,
                           md5('agri-market:district:KA:18')::uuid,
                           'Aland',
                           'KA-18-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-18:04')::uuid,
                           md5('agri-market:district:KA:18')::uuid,
                           'Chincholi',
                           'KA-18-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-18:05')::uuid,
                           md5('agri-market:district:KA:18')::uuid,
                           'Chitapur',
                           'KA-18-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-18:06')::uuid,
                           md5('agri-market:district:KA:18')::uuid,
                           'Jevargi',
                           'KA-18-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-18:07')::uuid,
                           md5('agri-market:district:KA:18')::uuid,
                           'Sedam',
                           'KA-18-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-18:08')::uuid,
                           md5('agri-market:district:KA:18')::uuid,
                           'Kamalapura',
                           'KA-18-08',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-18:09')::uuid,
                           md5('agri-market:district:KA:18')::uuid,
                           'Shahabad',
                           'KA-18-09',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-18:10')::uuid,
                           md5('agri-market:district:KA:18')::uuid,
                           'Kalgi',
                           'KA-18-10',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-18:11')::uuid,
                           md5('agri-market:district:KA:18')::uuid,
                           'Yedrami',
                           'KA-18-11',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- KODAGU
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-19:01')::uuid,
                           md5('agri-market:district:KA:19')::uuid,
                           'Madikeri',
                           'KA-19-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-19:02')::uuid,
                           md5('agri-market:district:KA:19')::uuid,
                           'Somwarpet',
                           'KA-19-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-19:03')::uuid,
                           md5('agri-market:district:KA:19')::uuid,
                           'Virajpet',
                           'KA-19-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-19:04')::uuid,
                           md5('agri-market:district:KA:19')::uuid,
                           'Ponnampet',
                           'KA-19-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-19:05')::uuid,
                           md5('agri-market:district:KA:19')::uuid,
                           'Kushalnagar',
                           'KA-19-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- KOLAR
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-20:01')::uuid,
                           md5('agri-market:district:KA:20')::uuid,
                           'Kolar',
                           'KA-20-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-20:02')::uuid,
                           md5('agri-market:district:KA:20')::uuid,
                           'Bangarapet',
                           'KA-20-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-20:03')::uuid,
                           md5('agri-market:district:KA:20')::uuid,
                           'Malur',
                           'KA-20-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-20:04')::uuid,
                           md5('agri-market:district:KA:20')::uuid,
                           'Mulbagal',
                           'KA-20-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-20:05')::uuid,
                           md5('agri-market:district:KA:20')::uuid,
                           'Srinivaspur',
                           'KA-20-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- KOPPAL
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-21:01')::uuid,
                           md5('agri-market:district:KA:21')::uuid,
                           'Koppal',
                           'KA-21-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-21:02')::uuid,
                           md5('agri-market:district:KA:21')::uuid,
                           'Gangavathi',
                           'KA-21-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-21:03')::uuid,
                           md5('agri-market:district:KA:21')::uuid,
                           'Kushtagi',
                           'KA-21-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-21:04')::uuid,
                           md5('agri-market:district:KA:21')::uuid,
                           'Yelburga',
                           'KA-21-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-21:05')::uuid,
                           md5('agri-market:district:KA:21')::uuid,
                           'Kanakagiri',
                           'KA-21-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-21:06')::uuid,
                           md5('agri-market:district:KA:21')::uuid,
                           'Karatagi',
                           'KA-21-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-21:07')::uuid,
                           md5('agri-market:district:KA:21')::uuid,
                           'Kukanur',
                           'KA-21-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- MANDYA
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-22:01')::uuid,
                           md5('agri-market:district:KA:22')::uuid,
                           'Mandya',
                           'KA-22-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-22:02')::uuid,
                           md5('agri-market:district:KA:22')::uuid,
                           'Maddur',
                           'KA-22-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-22:03')::uuid,
                           md5('agri-market:district:KA:22')::uuid,
                           'Malavalli',
                           'KA-22-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-22:04')::uuid,
                           md5('agri-market:district:KA:22')::uuid,
                           'Srirangapatna',
                           'KA-22-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-22:05')::uuid,
                           md5('agri-market:district:KA:22')::uuid,
                           'Krishnarajpet',
                           'KA-22-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-22:06')::uuid,
                           md5('agri-market:district:KA:22')::uuid,
                           'Nagamangala',
                           'KA-22-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-22:07')::uuid,
                           md5('agri-market:district:KA:22')::uuid,
                           'Pandavapura',
                           'KA-22-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- MYSURU
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-23:01')::uuid,
                           md5('agri-market:district:KA:23')::uuid,
                           'Mysuru',
                           'KA-23-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-23:02')::uuid,
                           md5('agri-market:district:KA:23')::uuid,
                           'Hunsur',
                           'KA-23-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-23:03')::uuid,
                           md5('agri-market:district:KA:23')::uuid,
                           'Krishnarajanagara',
                           'KA-23-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-23:04')::uuid,
                           md5('agri-market:district:KA:23')::uuid,
                           'Nanjangud',
                           'KA-23-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-23:05')::uuid,
                           md5('agri-market:district:KA:23')::uuid,
                           'Heggadadevankote',
                           'KA-23-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-23:06')::uuid,
                           md5('agri-market:district:KA:23')::uuid,
                           'Piriyapatna',
                           'KA-23-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-23:07')::uuid,
                           md5('agri-market:district:KA:23')::uuid,
                           'Tirumakudalu Narasipura',
                           'KA-23-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-23:08')::uuid,
                           md5('agri-market:district:KA:23')::uuid,
                           'Saraguru',
                           'KA-23-08',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-23:09')::uuid,
                           md5('agri-market:district:KA:23')::uuid,
                           'Saligrama',
                           'KA-23-09',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- RAICHUR
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-24:01')::uuid,
                           md5('agri-market:district:KA:24')::uuid,
                           'Raichur',
                           'KA-24-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-24:02')::uuid,
                           md5('agri-market:district:KA:24')::uuid,
                           'Sindhanur',
                           'KA-24-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-24:03')::uuid,
                           md5('agri-market:district:KA:24')::uuid,
                           'Manvi',
                           'KA-24-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-24:04')::uuid,
                           md5('agri-market:district:KA:24')::uuid,
                           'Devadurga',
                           'KA-24-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-24:05')::uuid,
                           md5('agri-market:district:KA:24')::uuid,
                           'Lingasugur',
                           'KA-24-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-24:06')::uuid,
                           md5('agri-market:district:KA:24')::uuid,
                           'Mudgal',
                           'KA-24-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-24:07')::uuid,
                           md5('agri-market:district:KA:24')::uuid,
                           'Maski',
                           'KA-24-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-24:08')::uuid,
                           md5('agri-market:district:KA:24')::uuid,
                           'Sirwar',
                           'KA-24-08',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- SHIVAMOGGA
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-25:01')::uuid,
                           md5('agri-market:district:KA:25')::uuid,
                           'Shivamogga',
                           'KA-25-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-25:02')::uuid,
                           md5('agri-market:district:KA:25')::uuid,
                           'Sagara',
                           'KA-25-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-25:03')::uuid,
                           md5('agri-market:district:KA:25')::uuid,
                           'Bhadravati',
                           'KA-25-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-25:04')::uuid,
                           md5('agri-market:district:KA:25')::uuid,
                           'Hosanagara',
                           'KA-25-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-25:05')::uuid,
                           md5('agri-market:district:KA:25')::uuid,
                           'Shikaripura',
                           'KA-25-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-25:06')::uuid,
                           md5('agri-market:district:KA:25')::uuid,
                           'Soraba',
                           'KA-25-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-25:07')::uuid,
                           md5('agri-market:district:KA:25')::uuid,
                           'Thirthahalli',
                           'KA-25-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- TUMAKURU
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-26:01')::uuid,
                           md5('agri-market:district:KA:26')::uuid,
                           'Tumakuru',
                           'KA-26-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-26:02')::uuid,
                           md5('agri-market:district:KA:26')::uuid,
                           'Chikkanayakanahalli',
                           'KA-26-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-26:03')::uuid,
                           md5('agri-market:district:KA:26')::uuid,
                           'Kunigal',
                           'KA-26-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-26:04')::uuid,
                           md5('agri-market:district:KA:26')::uuid,
                           'Madhugiri',
                           'KA-26-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-26:05')::uuid,
                           md5('agri-market:district:KA:26')::uuid,
                           'Sira',
                           'KA-26-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-26:06')::uuid,
                           md5('agri-market:district:KA:26')::uuid,
                           'Tiptur',
                           'KA-26-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-26:07')::uuid,
                           md5('agri-market:district:KA:26')::uuid,
                           'Gubbi',
                           'KA-26-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-26:08')::uuid,
                           md5('agri-market:district:KA:26')::uuid,
                           'Koratagere',
                           'KA-26-08',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-26:09')::uuid,
                           md5('agri-market:district:KA:26')::uuid,
                           'Pavagada',
                           'KA-26-09',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-26:10')::uuid,
                           md5('agri-market:district:KA:26')::uuid,
                           'Turuvekere',
                           'KA-26-10',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- UDUPI
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-27:01')::uuid,
                           md5('agri-market:district:KA:27')::uuid,
                           'Udupi',
                           'KA-27-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-27:02')::uuid,
                           md5('agri-market:district:KA:27')::uuid,
                           'Kapu',
                           'KA-27-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-27:03')::uuid,
                           md5('agri-market:district:KA:27')::uuid,
                           'Byndoor',
                           'KA-27-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-27:04')::uuid,
                           md5('agri-market:district:KA:27')::uuid,
                           'Karkala',
                           'KA-27-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-27:05')::uuid,
                           md5('agri-market:district:KA:27')::uuid,
                           'Kundapura',
                           'KA-27-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-27:06')::uuid,
                           md5('agri-market:district:KA:27')::uuid,
                           'Hebri',
                           'KA-27-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-27:07')::uuid,
                           md5('agri-market:district:KA:27')::uuid,
                           'Brahmavara',
                           'KA-27-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- UTTARA KANNADA
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-28:01')::uuid,
                           md5('agri-market:district:KA:28')::uuid,
                           'Karwar',
                           'KA-28-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-28:02')::uuid,
                           md5('agri-market:district:KA:28')::uuid,
                           'Sirsi',
                           'KA-28-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-28:03')::uuid,
                           md5('agri-market:district:KA:28')::uuid,
                           'Joida',
                           'KA-28-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-28:04')::uuid,
                           md5('agri-market:district:KA:28')::uuid,
                           'Dandeli',
                           'KA-28-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-28:05')::uuid,
                           md5('agri-market:district:KA:28')::uuid,
                           'Bhatkal',
                           'KA-28-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-28:06')::uuid,
                           md5('agri-market:district:KA:28')::uuid,
                           'Kumta',
                           'KA-28-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-28:07')::uuid,
                           md5('agri-market:district:KA:28')::uuid,
                           'Ankola',
                           'KA-28-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-28:08')::uuid,
                           md5('agri-market:district:KA:28')::uuid,
                           'Haliyal',
                           'KA-28-08',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-28:09')::uuid,
                           md5('agri-market:district:KA:28')::uuid,
                           'Honnavar',
                           'KA-28-09',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-28:10')::uuid,
                           md5('agri-market:district:KA:28')::uuid,
                           'Mundgod',
                           'KA-28-10',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-28:11')::uuid,
                           md5('agri-market:district:KA:28')::uuid,
                           'Siddapur',
                           'KA-28-11',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-28:12')::uuid,
                           md5('agri-market:district:KA:28')::uuid,
                           'Yellapur',
                           'KA-28-12',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- VIJAYAPURA
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-29:01')::uuid,
                           md5('agri-market:district:KA:29')::uuid,
                           'Vijayapura',
                           'KA-29-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-29:02')::uuid,
                           md5('agri-market:district:KA:29')::uuid,
                           'Indi',
                           'KA-29-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-29:03')::uuid,
                           md5('agri-market:district:KA:29')::uuid,
                           'Basavana Bagewadi',
                           'KA-29-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-29:04')::uuid,
                           md5('agri-market:district:KA:29')::uuid,
                           'Sindagi',
                           'KA-29-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-29:05')::uuid,
                           md5('agri-market:district:KA:29')::uuid,
                           'Muddebihal',
                           'KA-29-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-29:06')::uuid,
                           md5('agri-market:district:KA:29')::uuid,
                           'Talikote',
                           'KA-29-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-29:07')::uuid,
                           md5('agri-market:district:KA:29')::uuid,
                           'Devara Hipparagi',
                           'KA-29-07',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-29:08')::uuid,
                           md5('agri-market:district:KA:29')::uuid,
                           'Chadchan',
                           'KA-29-08',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-29:09')::uuid,
                           md5('agri-market:district:KA:29')::uuid,
                           'Tikota',
                           'KA-29-09',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-29:10')::uuid,
                           md5('agri-market:district:KA:29')::uuid,
                           'Babaleshwar',
                           'KA-29-10',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-29:11')::uuid,
                           md5('agri-market:district:KA:29')::uuid,
                           'Kolhar',
                           'KA-29-11',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-29:12')::uuid,
                           md5('agri-market:district:KA:29')::uuid,
                           'Nidagundi',
                           'KA-29-12',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-29:13')::uuid,
                           md5('agri-market:district:KA:29')::uuid,
                           'Almel',
                           'KA-29-13',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- YADGIR
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-30:01')::uuid,
                           md5('agri-market:district:KA:30')::uuid,
                           'Yadgir',
                           'KA-30-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-30:02')::uuid,
                           md5('agri-market:district:KA:30')::uuid,
                           'Shahapur',
                           'KA-30-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-30:03')::uuid,
                           md5('agri-market:district:KA:30')::uuid,
                           'Shorapur',
                           'KA-30-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-30:04')::uuid,
                           md5('agri-market:district:KA:30')::uuid,
                           'Gurmitkal',
                           'KA-30-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-30:05')::uuid,
                           md5('agri-market:district:KA:30')::uuid,
                           'Vadagera',
                           'KA-30-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-30:06')::uuid,
                           md5('agri-market:district:KA:30')::uuid,
                           'Hunsagi',
                           'KA-30-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;


-- ------------------------------------------------------------
-- VIJAYANAGARA
-- ------------------------------------------------------------

INSERT INTO taluks VALUES
                       (
                           md5('agri-market:taluk:KA-31:01')::uuid,
                           md5('agri-market:district:KA:31')::uuid,
                           'Hosapete',
                           'KA-31-01',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-31:02')::uuid,
                           md5('agri-market:district:KA:31')::uuid,
                           'Hagaribommanahalli',
                           'KA-31-02',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-31:03')::uuid,
                           md5('agri-market:district:KA:31')::uuid,
                           'Harapanahalli',
                           'KA-31-03',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-31:04')::uuid,
                           md5('agri-market:district:KA:31')::uuid,
                           'Hoovina Hadagali',
                           'KA-31-04',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-31:05')::uuid,
                           md5('agri-market:district:KA:31')::uuid,
                           'Kudligi',
                           'KA-31-05',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       ),
                       (
                           md5('agri-market:taluk:KA-31:06')::uuid,
                           md5('agri-market:district:KA:31')::uuid,
                           'Kotturu',
                           'KA-31-06',
                           TRUE,
                           CURRENT_TIMESTAMP,
                           CURRENT_TIMESTAMP
                       )
    ON CONFLICT (district_id, code) DO NOTHING;