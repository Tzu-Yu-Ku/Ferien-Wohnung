package fewodre.bookings;

import static org.hamcrest.CoreMatchers.*;

<<<<<<< HEAD
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingTests{
	@Autowired MockMvc mvc;
=======
import fewodre.catalog.holidayhomes.HolidayHome;
import fewodre.useraccounts.AccountEntity;
import fewodre.utils.Place;
import org.junit.Test;
>>>>>>> origin/prototype-ood

	@Test
	void preventsUnauthorizedAccess() throws Exception{

	}
}