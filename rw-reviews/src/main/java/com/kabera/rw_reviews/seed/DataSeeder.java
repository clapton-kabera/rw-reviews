package com.kabera.rw_reviews.seed;

import com.kabera.rw_reviews.model.Business;
import com.kabera.rw_reviews.model.Review;
import com.kabera.rw_reviews.model.ServiceOffering;
import com.kabera.rw_reviews.model.ServiceType;
import com.kabera.rw_reviews.repository.BusinessRepository;
import com.kabera.rw_reviews.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fills the database with sample businesses and reviews on every application start.
 * <p>
 * The H2 database is in-memory, so it is empty at each startup and this runner repopulates it.
 * Disable with app.seed.enabled=false (the test profile does this so tests control their own data).
 * <p>
 * A fixed random seed makes the generated reviews identical on every run, which keeps manual and
 * automated read/write testing predictable. All business names are fictional.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    /** Same seed => same ratings, texts and dates on every run. */
    private static final long RANDOM_SEED = 42L;

    private static final String[] FIRST_NAMES = {
            "Aline", "Eric", "Diane", "Jean Claude", "Grace", "Patrick", "Sandrine", "Olivier",
            "Claudine", "Emmanuel", "Josiane", "Prince", "Chantal", "Fabrice", "Belyse", "Didier"};
    private static final String[] LAST_INITIALS = {"U", "M", "N", "H", "K", "I", "G", "R", "B", "T"};

    // Review texts grouped by sentiment, so a review's text matches its star rating
    private static final String[] POSITIVE = {
            "Excellent service and very friendly staff. I will definitely come back!",
            "Great value for the price. Highly recommended to anyone in the area.",
            "Clean, professional and welcoming. One of the best in Rwanda.",
            "Everything was perfect from start to finish. Thank you!",
            "Quick service and quality that really stands out."};
    private static final String[] NEUTRAL = {
            "Decent overall, but nothing special. Service was a bit slow.",
            "Average experience. Fair prices, though there is room for improvement.",
            "It was okay. Staff were polite but we had to wait quite a while."};
    private static final String[] NEGATIVE = {
            "Disappointing. We waited a long time and the quality was not worth the price.",
            "Poor service and the place was not very clean. I would not return.",
            "Not what was advertised. Staff did not seem to care."};

    private final BusinessRepository businessRepository;
    private final ReviewRepository reviewRepository;

    public DataSeeder(BusinessRepository businessRepository, ReviewRepository reviewRepository) {
        this.businessRepository = businessRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Safety net: never duplicate data if the DB already has businesses
        // (e.g. someone later switches H2 to a file-based database)
        if (businessRepository.count() > 0) {
            log.info("Database already contains data - skipping seeding");
            return;
        }

        Random random = new Random(RANDOM_SEED);
        int totalReviews = 0;

        // The review count per business varies; the first one is large so pagination has many pages to walk
        List<Business> businesses = List.of(
                business("Inzozi Kitchen", ServiceType.RESTAURANT, "Gasabo", "KG 11 Ave, Kimironko", "+250788000001",
                        "Rwandan comfort food: brochettes, isombe and a daily lunch buffet.",
                        svc("Brochette plate", 3000, 6000), svc("Isombe with ugali", 4000, 7000), svc("Lunch buffet", 8000, 12000)),
                business("Umusozi Coffee House", ServiceType.CAFE, "Nyarugenge", "KN 4 St, City Centre", "+250788000002",
                        "Single-origin Rwandan coffee, fresh pastries and a quiet corner to work.",
                        svc("Coffee (cup)", 1500, 3500), svc("Pastries", 1000, 3000), svc("Breakfast set", 4000, 8000)),
                business("Ubuzima Wellness Spa", ServiceType.SPA, "Kicukiro", "KK 15 Rd, Kicukiro Centre", "+250788000003",
                        "Relaxing massages and skin care in a calm setting.",
                        svc("Swedish massage", 25000, 45000), svc("Facial", 20000, 35000), svc("Manicure", 8000, 15000)),
                business("Lakeview Guesthouse Kivu", ServiceType.GUESTHOUSE, "Rubavu", "Gisenyi Lake Kivu Shore", "+250788000004",
                        "Family-run guesthouse steps from the Lake Kivu beach.",
                        svc("Standard room (per night)", 40000, 60000), svc("Lake-view suite (per night)", 80000, 120000)),
                business("Gorilla Trails Rwanda", ServiceType.TOUR_OPERATOR, "Musanze", "Ruhengeri Town", "+250788000005",
                        "Guided hikes and cultural tours around Volcanoes National Park.",
                        svc("Mount Bisoke day hike", 80000, 150000), svc("Golden monkey tracking guide", 60000, 120000),
                        svc("Cultural village visit", 20000, 40000)),
                business("Kigali Style Barbers", ServiceType.SALON, "Gasabo", "KG 9 Ave, Remera", "+250788000006",
                        "Modern cuts, classic shaves and a friendly atmosphere.",
                        svc("Haircut", 3000, 6000), svc("Beard trim", 2000, 4000), svc("Hair colouring", 10000, 25000)),
                business("Huye Auto Care", ServiceType.GARAGE, "Huye", "Huye Main Road", "+250788000007",
                        "Honest repairs and routine servicing for all makes.",
                        svc("Oil change", 15000, 30000), svc("Brake inspection", 10000, 25000), svc("Full service", 60000, 120000)),
                business("Imbuto Clinic", ServiceType.HEALTH_CLINIC, "Nyarugenge", "KN 3 Ave, Nyarugenge", "+250788000008",
                        "General practice and dental care with short waiting times.",
                        svc("General consultation", 5000, 15000), svc("Dental check-up", 10000, 30000), svc("Lab tests", 3000, 40000)),
                business("Amahoro Heights Hotel", ServiceType.HOTEL, "Nyarugenge", "KN 5 Rd, Kiyovu", "+250788000009",
                        "Business hotel with conference facilities and a rooftop restaurant.",
                        svc("Standard room (per night)", 70000, 100000), svc("Executive room (per night)", 120000, 180000),
                        svc("Conference hall (per day)", 200000, 500000)));

        // Persist businesses first (offerings are saved via cascade) so each has an id for its reviews
        businessRepository.saveAll(businesses);

        int[] reviewCounts = {120, 45, 30, 25, 40, 35, 20, 28, 60};
        for (int i = 0; i < businesses.size(); i++) {
            List<Review> reviews = generateReviews(businesses.get(i), reviewCounts[i], random);
            reviewRepository.saveAll(reviews);
            totalReviews += reviews.size();
        }

        log.info("Seeded {} businesses and {} reviews", businesses.size(), totalReviews);
    }

    /** Builds a business (not yet saved) with its service offerings. */
    private Business business(String name, ServiceType type, String district, String address, String phone,
                              String description, ServiceOffering... offerings) {
        Business b = new Business();
        b.setName(name);
        b.setServiceType(type);
        b.setDistrict(district);
        b.setAddress(address);
        b.setPhone(phone);
        b.setDescription(description);
        for (ServiceOffering o : offerings) {
            b.addService(o);
        }
        return b;
    }

    private ServiceOffering svc(String name, long minPrice, long maxPrice) {
        return new ServiceOffering(name, minPrice, maxPrice);
    }

    /** Creates {@code count} reviews with realistic rating spread, names, text and dates (last year). */
    private List<Review> generateReviews(Business business, int count, Random random) {
        List<Review> reviews = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int rating = randomRating(random);
            String name = pick(FIRST_NAMES, random) + " " + pick(LAST_INITIALS, random) + ".";
            // About 1 in 6 reviews is just a star rating without text
            String comment = random.nextInt(6) == 0 ? null : commentFor(rating, random);
            // Random moment within the past year so "newest first" ordering is meaningful
            Instant createdAt = Instant.now()
                    .minus(random.nextInt(365), ChronoUnit.DAYS)
                    .minus(random.nextInt(24 * 60), ChronoUnit.MINUTES);
            reviews.add(new Review(business, name, rating, comment, createdAt));
        }
        return reviews;
    }

    /** Skewed towards 4-5 stars, like real review data: ~10% 1-star ... ~35% 5-star. */
    private int randomRating(Random random) {
        int roll = random.nextInt(100);
        if (roll < 8) return 1;
        if (roll < 18) return 2;
        if (roll < 35) return 3;
        if (roll < 65) return 4;
        return 5;
    }

    private String commentFor(int rating, Random random) {
        if (rating >= 4) return pick(POSITIVE, random);
        if (rating == 3) return pick(NEUTRAL, random);
        return pick(NEGATIVE, random);
    }

    private String pick(String[] options, Random random) {
        return options[random.nextInt(options.length)];
    }
}
