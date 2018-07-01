import lenz.htw.zpifub.net.NetworkClient;

public class StrategicMap {

	MapFrame mapframe;
	NetworkClient client;
	private BoardConfig config;
	
	public StrategicMap(NetworkClient client, BoardConfig config, boolean display) {
		this.client = client;
		mapframe = new MapFrame(Util.BOARD_SIZE);		
		mapframe.setVisible(display);
		this.config = config;
		config.layer = new int[10][][];
		int size  = Util.BOARD_SIZE;
		for(int i = 0; i < config.layer.length; i++){
			config.layer[i] = new int[size][size];
			size = size / 2;
		}
	}
	
	public void update(){
		for(int x = 0; x<1024; x++){
			for(int y = 0; y<1024; y++){
				config.layer[0][x][y] = client.getBoard(x, y);					
			}	
		}

		for(int l = 0; l < 9; l++){

			for(int x = 0; x<config.layer[l].length; x+=2){
				for(int y = 0; y<config.layer[l].length; y+=2){
					int r =(((config.layer[l][x    ][y    ] >> 16) & 0x000000ff) + 
							((config.layer[l][x + 1][y    ] >> 16) & 0x000000ff) + 
							((config.layer[l][x    ][y + 1] >> 16) & 0x000000ff) + 
							((config.layer[l][x + 1][y + 1] >> 16) & 0x000000ff)) >> 2;
					
					int g =(((config.layer[l][x    ][y    ] >> 8 ) & 0x000000ff) + 
							((config.layer[l][x + 1][y    ] >> 8 ) & 0x000000ff) + 
							((config.layer[l][x    ][y + 1] >> 8 ) & 0x000000ff) + 
							((config.layer[l][x + 1][y + 1] >> 8 ) & 0x000000ff)) >> 2;
							
					int b =(((config.layer[l][x    ][y    ] >> 0 ) & 0x000000ff) + 
							((config.layer[l][x + 1][y    ] >> 0 ) & 0x000000ff) + 
							((config.layer[l][x    ][y + 1] >> 0 ) & 0x000000ff) + 
							((config.layer[l][x + 1][y + 1] >> 0 ) & 0x000000ff)) >> 2;		
					
					config.layer[l+1][x>>1][y>>1] = (r <<16 | g << 8 | b);
				}
			}
		}
		
		//update2(0,0,1024);
	}
	
	public void update2(int xIn, int yIn, int size){
		refreshLayer(0,256,50,50);
		refreshLayer(1,256,0,0);
		refreshLayer(1,256,256,0);
		refreshLayer(1,256,256,256);
		refreshLayer(1,256,0,256);
		if(size>2) update2(xIn, yIn, size >> 2);
//		if(xIn<)
//		for(int x = 0; x<size; x++){
//			for(int y = 0; y<size; y++){
//				layer[0][x][y] = client.getBoard(x, y);					
//			}	
//		}		
	}
	
	public void refreshLayer(int layerN,int size, int xOffset, int yOffset){
		if(layerN == 0){
			for(int x = 0; x<size; x++){
				for(int y = 0; y<size; y++){
					config.layer[0][x+xOffset][y+yOffset] = client.getBoard(x+xOffset, y+yOffset);					
				}	
			}
		} else {
			int l = layerN - 1;
			for(int x = xOffset; x < size + xOffset; x++){
				for(int y = yOffset; y < size + yOffset; y++){
					int r =(((config.layer[l][x    ][y    ] >> 16) & 0x000000ff) + 
							((config.layer[l][x + 1][y    ] >> 16) & 0x000000ff) + 
							((config.layer[l][x    ][y + 1] >> 16) & 0x000000ff) + 
							((config.layer[l][x + 1][y + 1] >> 16) & 0x000000ff)) >> 2;
					
					int g =(((config.layer[l][x    ][y    ] >> 8 ) & 0x000000ff) + 
							((config.layer[l][x + 1][y    ] >> 8 ) & 0x000000ff) + 
							((config.layer[l][x    ][y + 1] >> 8 ) & 0x000000ff) + 
							((config.layer[l][x + 1][y + 1] >> 8 ) & 0x000000ff)) >> 2;
							
					int b =(((config.layer[l][x    ][y    ] >> 0 ) & 0x000000ff) + 
							((config.layer[l][x + 1][y    ] >> 0 ) & 0x000000ff) + 
							((config.layer[l][x    ][y + 1] >> 0 ) & 0x000000ff) + 
							((config.layer[l][x + 1][y + 1] >> 0 ) & 0x000000ff)) >> 2;		
					
					config.layer[layerN][x>>1][y>>1] = (r <<16 | g << 8 | b);			
				}	
			}
		}
	}
	
	public void render() {
		update();
		int offsetX = 0;
		int offsetY = 0;
		int size = 1;
		int offsetIncrease = 1024;		
		for(int l = 0; l < 10; l++) {
			if(l<5) {
				for(int y = 0; y < config.layer[l].length; y+=size){
					for(int x = 0; x < config.layer[l].length; x += size){			
						mapframe.setPixel((x/size) + offsetX, (y/size) + offsetY, config.layer[l][x][y]);
					}
				}
				//size = size << 1;
				if(l % 2 == 0) offsetX += offsetIncrease;
				else offsetY += offsetIncrease;
				offsetIncrease = offsetIncrease >> 1;
			} else {
				offsetX = 1600;
				if(l==5 )offsetY = 0;
				else offsetY = (l-4)*140+50;
				for(int y = 0; y < config.layer[l].length*8; y++){
					for(int x = 0; x < config.layer[l].length*8; x ++){			
						mapframe.setPixel((x) + offsetX, (y) + offsetY, config.layer[l][x/8][y/8]);
					}
				}
			}
		}
		mapframe.invalidate();
	}
}
