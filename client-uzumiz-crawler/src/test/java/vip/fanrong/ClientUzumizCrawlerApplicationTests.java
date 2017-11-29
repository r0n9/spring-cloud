package vip.fanrong;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import vip.fanrong.mapper.ZmzResourceTopMapper;
import vip.fanrong.model.ZmzResourceTop;
import vip.fanrong.service.ZmzCrawlerService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientUzumizCrawlerApplicationTests {

	@Autowired
	private ZmzResourceTopMapper zmzResourceTopMapper;

	@Autowired
	private ZmzCrawlerService zmzCrawlerService;

	@Test
	@Rollback
	public void testInsert() throws Exception {
		ZmzResourceTop data = new ZmzResourceTop();
		data.setCount(1);
		data.setGetTime(new Date());
		data.setName("Test");
		data.setNameEn("Test in EN");
		data.setSrc("http://xxx");

		System.out.println(zmzResourceTopMapper.insert(data));
	}


	@Test
	@Rollback
	public void testInsertBatch() throws Exception {
		ZmzResourceTop data1 = new ZmzResourceTop();
		data1.setCount(1);
		data1.setGetTime(new Date());
		data1.setName("Test");
		data1.setNameEn("Test in EN");
		data1.setSrc("http://xxx");
		ZmzResourceTop data2 = new ZmzResourceTop();
		data2.setCount(2);
		data2.setGetTime(new Date());
		data2.setName("Test2");
		data2.setNameEn("Test in EN2");
		data2.setSrc("http://xxx2");

		List<ZmzResourceTop> list = new ArrayList<>();
		list.add(data1);
		list.add(data2);

		System.out.println(zmzResourceTopMapper.batchInsert(list));
	}

	@Test
	public void testService(){
		zmzCrawlerService.getZmzResouceTops();
	}
}
