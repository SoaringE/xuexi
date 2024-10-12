package main

import (
	"encoding/json"
	"fmt"
	"github.com/gin-gonic/gin"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
	"log"
	"net/http"
	"os"
	"strings"

	"github.com/elastic/go-elasticsearch/v8"
)

type User struct {
	gorm.Model
	Email    string
	Password string
}

type News struct {
	gorm.Model
	Title string
	Time  string
	Link  string
}

type NewsCategory struct {
	CategoryKeyword     string `json:"categoryKeyword"`
	IsAbsolutePath      int    `json:"isAbsolutePath"`
	PreviewDomain       string `json:"previewDomain"`
	TerminalRoot        string `json:"terminalRoot"`
	IsContent           int    `json:"isContent"`
	CategoryDisplayName string `json:"categoryDisplayName"`
	ListUrl             string `json:"listUrl"`
	TerminalId          string `json:"terminalId"`
	CategoryLogo        string `json:"categoryLogo"`
	CategoryName        string `json:"categoryName"`
	CategoryDesc        string `json:"categoryDesc"`
	CategoryType        string `json:"categoryType"`
	TerminalName        string `json:"terminalName"`
	SortType            string `json:"sortType"`
	PublishDomain       string `json:"publishDomain"`
	CategoryUrl         string `json:"categoryUrl"`
	CategoryId          string `json:"categoryId"`
	Datasource          []struct {
		ResponsibleEditor string `json:"responsibleEditor"`
		Keywords          string `json:"keywords"`
		ContentId         string `json:"contentId"`
		SourceLink        string `json:"sourceLink"`
		Title             string `json:"title"`
		Content           string `json:"content"`
		PublishUrl        string `json:"publishUrl"`
		LinkUrls          []struct {
			LinkTitle string `json:"linkTitle"`
			LinkUrl   string `json:"linkUrl"`
		} `json:"linkUrls"`
		ShowAttribute     string        `json:"showAttribute"`
		Quote             string        `json:"quote"`
		ContentType       string        `json:"contentType"`
		Slug              string        `json:"slug"`
		Summary           string        `json:"summary"`
		Editor            string        `json:"editor"`
		PublishTime       string        `json:"publishTime"`
		ContentAudiosList []interface{} `json:"contentAudiosList"`
		Author            string        `json:"author"`
		ContentVideosList []interface{} `json:"contentVideosList"`
		TitleImages       []struct {
			IsPrimary string `json:"isPrimary"`
			ImageUrl  string `json:"imageUrl"`
		} `json:"titleImages"`
		MediaNumber    string        `json:"mediaNumber"`
		SourceText     string        `json:"sourceText"`
		ShareImages    []interface{} `json:"shareImages"`
		IsOriginal     string        `json:"isOriginal"`
		PublishType    string        `json:"publishType"`
		MultimediaLink string        `json:"multimediaLink"`
		ShowTitle      string        `json:"showTitle"`
		Subtitle       string        `json:"subtitle"`
		ContentSort    string        `json:"contentSort"`
		Style          string        `json:"style"`
		NewsLabel      string        `json:"newsLabel"`
		Poster         string        `json:"poster"`
		CategoryId     string        `json:"categoryId"`
	} `json:"datasource"`
}

type Article struct {
	ID    string `json:"id"`
	Title string `json:"title"`
}

func handleSearch(es *elasticsearch.Client) gin.HandlerFunc {
	return func(c *gin.Context) {
		// 从 URL 查询参数中获取搜索关键字
		query := c.Query("query")
		if query == "" {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Missing query parameter"})
			return
		}

		// 构建搜索请求
		var buf strings.Builder
		buf.WriteString(`{
            "query": {
                "match": {
                    "title": {
                        "query": "` + query + `",
                        "fuzziness": "AUTO"
                    }
                }
            }
        }`)

		// 发送搜索请求到 Elasticsearch
		res, err := es.Search(
			es.Search.WithIndex("articles"),
			es.Search.WithBody(strings.NewReader(buf.String())),
		)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Elasticsearch error"})
			log.Println("Elasticsearch error:", err)
			return
		}
		defer res.Body.Close()

		// 解析搜索结果
		var response map[string]interface{}
		if err := json.NewDecoder(res.Body).Decode(&response); err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to parse Elasticsearch response"})
			log.Println("Failed to parse Elasticsearch response:", err)
			return
		}

		// 提取命中的文章信息
		var articles []Article
		for _, hit := range response["hits"].(map[string]interface{})["hits"].([]interface{}) {
			source := hit.(map[string]interface{})["_source"].(map[string]interface{})
			articles = append(articles, Article{
				ID:    hit.(map[string]interface{})["_id"].(string),
				Title: source["title"].(string),
			})
		}

		// 将文章列表返回给客户端
		c.JSON(http.StatusOK, articles)
	}
}

func main() {
	db, err := gorm.Open(sqlite.Open("test.db"), &gorm.Config{})
	if err != nil {
		panic("failed to connect database")
	}
	db.AutoMigrate(&User{})
	db.Table("activity_news").AutoMigrate(&News{})
	db.Table("meeting_news").AutoMigrate(&News{})
	db.Table("visit_news").AutoMigrate(&News{})
	db.Table("survey_news").AutoMigrate(&News{})
	db.Table("article_news").AutoMigrate(&News{})
	db.Table("speech_news").AutoMigrate(&News{})
	db.Table("command_news").AutoMigrate(&News{})

	es, err := elasticsearch.NewDefaultClient()
	if err != nil {
		log.Fatal("Elasticsearch connection error:", err)
	}

	r := gin.Default()

	r.GET("/json", func(c *gin.Context) {
		data := map[string]interface{}{
			"lang": "en",
			"id":   "123456",
		}
		c.AsciiJSON(http.StatusOK, data)
	})

	api := r.Group("/api")
	{

		api.POST("/login", func(c *gin.Context) {
			var body struct {
				Email    string `json:"email"`
				Password string `json:"password"`
			}

			//绑定json和结构体
			if err := c.BindJSON(&body); err != nil {
				return
			}
			//获取json中的key,注意使用 . 访问
			email := body.Email
			password := body.Password
			if email != "" && password != "" {
				log.Printf("email=%s", email)
				log.Printf("password=%s", password)
				var user User
				result := db.First(&user, "email = ?", email)
				if result.Error != nil {
					// new user
					user := User{Email: email, Password: password}
					db.Create(&user)
					response := map[string]interface{}{
						"state": "success",
						"id":    user.ID,
					}
					c.AsciiJSON(http.StatusOK, response)
				} else {
					if user.Password == password {
						// success
						response := map[string]interface{}{
							"state": "success",
							"id":    user.ID,
						}
						c.AsciiJSON(http.StatusOK, response)
					} else {
						// wrong password
						response := map[string]interface{}{
							"state": "failure",
							"id":    0,
						}
						c.AsciiJSON(http.StatusOK, response)
					}
				}
			}
		})

		api.POST("/news", func(c *gin.Context) {
			var body struct {
				Start    int    `json:"start"`
				MaxNum   int    `json:"maxNum"`
				Category string `json:"category"`
			}
			if err := c.BindJSON(&body); err != nil {
				return
			}
			//获取json中的key,注意使用 . 访问
			start := body.Start
			maxNum := body.MaxNum
			category := body.Category

			var newsList []News
			var end = false
			for i := 1; i <= maxNum; i++ {
				var news News
				result := db.Table(fmt.Sprintf(`%s_news`, category)).First(&news, "id = ?", start+i)

				if result.Error != nil {
					dataBytes, err := os.ReadFile(fmt.Sprintf(`./news/%s.json`, category))
					if err != nil {
						log.Fatalf("Failed to read JSON file: %v", err)
					}
					var newsCategory NewsCategory
					err = json.Unmarshal(dataBytes, &newsCategory)
					if err != nil {
						fmt.Println("Failed to parse JSON:", err)
						return
					}
					if start+i > len(newsCategory.Datasource) {
						end = true
						break
					}
					// fmt.Println(start + i)
					news := News{
						Title: newsCategory.Datasource[start+i-1].Title,
						Time:  newsCategory.Datasource[start+i-1].PublishTime,
						Link:  newsCategory.Datasource[start+i-1].PublishUrl,
					}
					db.Table(fmt.Sprintf(`%s_news`, category)).Create(&news)
					newsList = append(newsList, news)
				} else {
					newsList = append(newsList, news)
				}

			}
			// fmt.Println(newsList)
			response := map[string]interface{}{
				"newsProfilesList": newsList,
				"end":              end,
			}
			c.JSON(http.StatusOK, response)

		})

		api.GET("/search", handleSearch(es))
	}

	r.Run(":8080")

	/*
		dataBytes, err := os.ReadFile(fmt.Sprintf("./news/activity.json"))
		if err != nil {
			log.Fatalf("Failed to read JSON file: %v", err)
		}

		var newsCategory NewsCategory
		err = json.Unmarshal(dataBytes, &newsCategory)
		if err != nil {
			fmt.Println("Failed to parse JSON:", err)
			return
		}

		// 打印解析后的数据
		// fmt.Println(newsCategory.CategoryName)
		fmt.Println(newsCategory.Datasource[0].LinkUrls[0].LinkTitle)
				*
	*/
}
