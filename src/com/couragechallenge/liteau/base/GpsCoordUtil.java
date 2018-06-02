package com.couragechallenge.liteau.base;



public class GpsCoordUtil{
	static double pi = 3.14159265358979324;
	static double a = 6378245.0;
	static double ee = 0.00669342162296594323;
	static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

	////WGS-84 to GCJ-02
	public static double[] wgs2gcj(double lat, double lon) {
		if (outOfChina(lat, lon)){
			return new double[]{lat, lon};
		}
		double[] d= delta(lat,lon);

		return new double[]{lat+d[0],lon+d[1]};
	}

	//WGS-84 to Baidu
	public static double[] wgs2bd(double lat, double lon) {
		double[] wgs2gcj = wgs2gcj(lat, lon);
		double[] gcj2bd = gcj2bd(wgs2gcj[0], wgs2gcj[1]);
		return gcj2bd;
	}

	//GCJ-02 to WGS
	public static double[] gcj2wgs(double lat,double lon){
		if (outOfChina(lat, lon)){
			return new double[]{lat, lon};
		}
		double[] d= delta(lat,lon);

		return new double[]{lat-d[0],lon-d[1]};
	}

	//GCJ-02 to Baidu
	public static double[] gcj2bd(double lat, double lon) {
		double x = lon, y = lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		double bd_lon = z * Math.cos(theta) + 0.0065;
		double bd_lat = z * Math.sin(theta) + 0.006;
		return new double[] {bd_lat, bd_lon };
	}

	//Baidu to WGS-84
	public static double[] bd2wgs(double lat,double lon){
		double[] bd2gcj = bd2gcj(lat, lon);
		double[] gcj2wgs = gcj2wgs(bd2gcj[0], bd2gcj[1]);
		return gcj2wgs;
	}

	//Baidu to GCJ-02
	public static double[] bd2gcj(double lat, double lon) {
		double x = lon - 0.0065, y = lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		double gg_lon = z * Math.cos(theta);
		double gg_lat = z * Math.sin(theta);
		return new double[] { gg_lat, gg_lon };
	}

	public static double distance (double latA, double logA, double latB, double logB) {
		double earthR = 6371000;
		double x = Math.cos(latA * Math.PI / 180) * Math.cos(latB * Math.PI / 180) * Math.cos((logA - logB) * Math.PI / 180);
		double y = Math.sin(latA * Math.PI / 180) * Math.sin(latB * Math.PI / 180);
		double s = x + y;
		if (s > 1)
			s = 1;
		if (s < -1)
			s = -1;
		double alpha = Math.acos(s);
		double distance = alpha * earthR;
		return distance;
	}

	public static boolean outOfChina(double wgsLat,double wgsLon){
		if (wgsLon < 72.004 || wgsLon > 137.8347)
			return true;
		if (wgsLat < 0.8293 || wgsLat > 55.8271)
			return true;
		return false;
	}

	private static double[] delta(double lat, double lon) {
		double a = 6378245.0d;
		double ee = 0.00669342162296594323d;
		double dLat = transformLat(lon - 105.0, lat - 35.0);
		double dLon = transformLon(lon - 105.0, lat - 35.0);
		double radLat = lat / 180.0 * pi;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
		dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
		return new double[]{dLat,dLon};
	}

	private static double transformLat(double lat, double lon) {
		double ret = -100.0 + 2.0 * lat + 3.0 * lon + 0.2 * lon * lon + 0.1 * lat * lon + 0.2 * Math.sqrt(Math.abs(lat));
		ret += (20.0 * Math.sin(6.0 * lat * pi) + 20.0 * Math.sin(2.0 * lat * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lon * pi) + 40.0 * Math.sin(lon / 3.0 * pi)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(lon / 12.0 * pi) + 320 * Math.sin(lon * pi  / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	private static double transformLon(double lat, double lon) {
		double ret = 300.0 + lat + 2.0 * lon + 0.1 * lat * lat + 0.1 * lat * lon + 0.1 * Math.sqrt(Math.abs(lat));
		ret += (20.0 * Math.sin(6.0 * lat * pi) + 20.0 * Math.sin(2.0 * lat * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lat * pi) + 40.0 * Math.sin(lat / 3.0 * pi)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(lat / 12.0 * pi) + 300.0 * Math.sin(lat / 30.0 * pi)) * 2.0 / 3.0;
		return ret;
	}

	/**返回为double[lat,lon]*/
	public  static double[] mercator2LatLon(double x,double y){
		double lon = x / 20037508.34 * 180;
		double lat = y / 20037508.34 * 180;
		lat = 180 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180)) - Math.PI / 2);

		double[] latLng = new double[2];
		latLng[0] = lat;
		latLng[1] = lon;

		return latLng;
	}

	public  static double[] latLon2Mercator(double lat,double lon){
		double x = lon *20037508.34/180;
		double y = Math.log(Math.tan((90+lat)*Math.PI/360))/(Math.PI/180);
		y = y *20037508.34/180;

		return new double[]{x,y};
	}

	private static final double[][] MC2LL = {
		{1.410526172116255e-8, 898305509648872e-20, -1.9939833816331, 200.9824383106796, -187.2403703815547, 91.6087516669843, -23.38765649603339, 2.57121317296198, -.03801003308653, 17337981.2},
		{-7.435856389565537e-9, 8983055097726239e-21, -.78625201886289, 96.32687599759846, -1.85204757529826, -59.36935905485877, 47.40033549296737, -16.50741931063887, 2.28786674699375, 10260144.86},
		{-3.030883460898826e-8, 898305509983578e-20, .30071316287616, 59.74293618442277, 7.357984074871, -25.38371002664745, 13.45380521110908, -3.29883767235584, .32710905363475, 6856817.37},
		{-1.981981304930552e-8, 8983055099779535e-21, .03278182852591, 40.31678527705744, .65659298677277, -4.44255534477492, .85341911805263, .12923347998204, -.04625736007561, 4482777.06},
		{3.09191371068437e-9, 8983055096812155e-21, 6995724062e-14, 23.10934304144901, -.00023663490511, -.6321817810242, -.00663494467273, .03430082397953, -.00466043876332, 2555164.4},
		{2.890871144776878e-9, 8983055095805407e-21, -3.068298e-8, 7.47137025468032, -353937994e-14, -.02145144861037, -1234426596e-14, .00010322952773, -323890364e-14, 826088.5}
	};
	private static final double[][] LL2MC = {
		{-.0015702102444, 111320.7020616939, 1704480524535203.0, -10338987376042340.0, 26112667856603880.0, -35149669176653700.0, 26595700718403920.0, -10725012454188240.0, 1800819912950474.0, 82.5},
		{.0008277824516172526, 111320.7020463578, 647795574.6671607, -4082003173.641316, 10774905663.51142, -15171875531.51559, 12053065338.62167, -5124939663.577472, 913311935.9512032, 67.5},
		{.00337398766765, 111320.7020202162, 4481351.045890365, -23393751.19931662, 79682215.47186455, -115964993.2797253, 97236711.15602145, -43661946.33752821, 8477230.501135234, 52.5},
		{.00220636496208, 111320.7020209128, 51751.86112841131, 3796837.749470245, 992013.7397791013, -1221952.21711287, 1340652.697009075, -620943.6990984312, 144416.9293806241, 37.5},
		{-.0003441963504368392, 111320.7020576856, 278.2353980772752, 2485758.690035394, 6070.750963243378, 54821.18345352118, 9540.606633304236, -2710.55326746645, 1405.483844121726, 22.5},
		{-.0003218135878613132, 111320.7020701615, .00369383431289, 823725.6402795718, .46104986909093, 2351.343141331292, 1.58060784298199, 8.77738589078284, .37238884252424, 7.45}
	};
	private static final double[] MCBAND = {12890594.86, 8362377.87, 5591021, 3481989.83, 1678043.12, 0};
	private static final int[] LLBAND = {75, 60, 45, 30, 15, 0};

	private static double[] convertor(double[] tLatLng, double[] e) {
		if (tLatLng != null && e != null) {
			double i = e[0] + e[1] * Math.abs(tLatLng[1]),
					n = Math.abs(tLatLng[0]) / e[9],
					a = e[2] + e[3] * n + e[4] * n * n + e[5] * n * n * n + e[6] * n * n * n * n + e[7] * n * n * n * n * n + e[8] * n * n * n * n * n * n;
			i *= tLatLng[1] < 0 ? -1 : 1;
			a *= tLatLng[0] < 0 ? -1 : 1;
			return new double[]{a, i};
		}

		return null;
	}

	private static double getLoop(double t, double e, double i) {
		for(; t > i;) {
			t -= i - e;
		}
		for (; e > t;) {
			t += i - e;
		}
		return t;
	}

	private static double getRange(double t, double e, double i) {
		t = Math.max(t, e);
		t = Math.min(t, i);
		return t;
	}

	/**	平面坐标(米制坐标)转经纬度坐标,返回double[lat,lng] */
	public static double[] bdMercator2LatLon(double[] tXY) {
		double[] e = new double[]{Math.abs(tXY[0]), Math.abs(tXY[1])};
		double[] i = null;
		for (int n = 0; n < MCBAND.length; n++) {
			if (e[0]>= MCBAND[n]) {
				i = MC2LL[n];
				break;
			}
		}
		return convertor(tXY, i);
	}

	/**	经纬度转平面(米制坐标),返回double[x,y]	*/
	public static double[] bdLatLon2Mercator(double[] tLatLng) {		
		double[] i = null;
		tLatLng[1] = getLoop(tLatLng[1], -180, 180);
		tLatLng[0]= getRange(tLatLng[0], -74, 74);
		double[] e = tLatLng;
		for (int n = 0; n < LLBAND.length; n++) {
			if (e[0]>= LLBAND[n]) {
				i = LL2MC[n];
				break;
			}
		}

		if (i == null) {
			for (int n = LLBAND.length - 1; n >= 0; n--) {
				if (e[0]<= -LLBAND[n]) {
					i = LL2MC[n];
					break;
				}
			}
		}

		return convertor(tLatLng, i);
	}

}
